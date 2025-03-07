package org.oops.api.redis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.oops.api.coin.dto.CoinPriceDTO;
import org.oops.api.email.service.EmailService;
import org.oops.domain.alert.Alert;
import org.oops.domain.alert.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class PriceUpdateSubscriber implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(PriceUpdateSubscriber.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AlertRepository alertRepository;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate; // Redis 활용해서 중복 메일 발송 발지

    private static final long ALERT_TTL = 5; // 알림 유지 시간 (5분)
    private static final long ALERT_CACHE_TTL = 5;  // DB 조회 캐싱 TTL (5분)

    public PriceUpdateSubscriber(AlertRepository alertRepository, EmailService emailService, StringRedisTemplate redisTemplate) {
        this.alertRepository = alertRepository;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message.getBody());
            CoinPriceDTO priceDTO = objectMapper.treeToValue(jsonNode, CoinPriceDTO.class);

            // [1] Redis에서 알림 목록 캐싱 확인 (캐시가 있다면 DB 조회 생략)
            String cacheKey = "alert_list:" + priceDTO.getCode();
            List<Alert> alerts;

            if (redisTemplate.hasKey(cacheKey)) {
                logger.info("⚡ Redis 캐싱된 알림 데이터 사용: {}", cacheKey);
                String cachedAlerts = redisTemplate.opsForValue().get(cacheKey);
                alerts = objectMapper.readValue(cachedAlerts, objectMapper.getTypeFactory().constructCollectionType(List.class, Alert.class));
            } else {
                // 캐시가 없을 경우 강제로 새로 불러오기
                logger.info("🛠️ DB에서 알림 조회 후 Redis 캐싱: {}", priceDTO.getCode());
                alerts = alertRepository.findByCoinTickerAndAlertActiveTrue(priceDTO.getCode());
                redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(alerts), ALERT_CACHE_TTL, TimeUnit.MINUTES);
            }

            // [2] 이메일 중복 발송 방지
            for (Alert alert : alerts) {
                boolean conditionMet = false;

                if ("BELOW".equals(alert.getAlertCondition())) {
                    conditionMet = BigDecimal.valueOf(priceDTO.getPrice()).compareTo(alert.getAlertPrice()) <= 0;
                } else if ("ABOVE".equals(alert.getAlertCondition())) {
                    conditionMet = BigDecimal.valueOf(priceDTO.getPrice()).compareTo(alert.getAlertPrice()) > 0;
                }

                if (conditionMet) {
                    // Redis에서 최근 알림 발송 여부 체크
                    String redisKey = "alert:" + alert.getUserId() + ":" + alert.getCoinTicker();
                    if (redisTemplate.hasKey(redisKey)) {
                        logger.info("⚠️ 이미 최근에 알림 발송됨 (5분 이내), 이메일 생략: {}", redisKey);
                        continue;
                    }

                    logger.info("🔔 알림 조건 충족! 사용자: {}, 코인: {}, 설정 가격: {}, 현재 가격: {}, 조건: {}",
                            alert.getUserId(), alert.getCoinTicker(), alert.getAlertPrice(), priceDTO.getPrice(), alert.getAlertCondition());

                    // 이메일 제목 및 내용 구성
                    String subject = "[coinfo] 코인 가격 알림";
                    String content = "알림 설정한 코인 : " + alert.getCoinName() + "(" + alert.getCoinTicker() + ")의 가격이 설정 가격" +
                            (alert.getAlertCondition().equals("BELOW") ? "보다 하락" : "을 초과") +
                            "하였습니다. 현재 가격: " + priceDTO.getPrice() + "원";

                    // 이메일 알림 전송
                    emailService.sendEmailNotice(alert.getEmail(), subject, content);

                    // [3] Redis에 발송 기록 저장 (5분 TTL)
                    redisTemplate.opsForValue().set(redisKey, "sent", ALERT_TTL, TimeUnit.MINUTES);

                    // [4] 알림 비활성화
                    alert.updateAlertActive(false);
                    alertRepository.save(alert);
                    redisTemplate.delete("alert_list:" + priceDTO.getCode());
                }
            }
        } catch (Exception e) {
            logger.error("❌ Redis 메시지 처리 중 오류 발생", e);
        }
    }
}