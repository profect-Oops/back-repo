package org.oops.api.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oops.api.coin.dto.CoinPriceDTO;
import org.oops.api.email.service.EmailService;
import org.oops.domain.alert.Alert;
import org.oops.domain.alert.AlertRepository;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceUpdateSubscriberTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private Message message;

    private PriceUpdateSubscriber priceUpdateSubscriber;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        priceUpdateSubscriber = new PriceUpdateSubscriber(alertRepository, emailService, redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void onMessage_PriceBelowThreshold_EmailSent() throws Exception {
        // Given: Redis에 캐시된 데이터 없음
        String coinTicker = "BTC";
        String cacheKey = "alert_list:" + coinTicker;
        when(redisTemplate.hasKey(cacheKey)).thenReturn(false);

        // 알림 조건을 만족하는 Alert 설정
        Alert alert = Alert.builder()
                .alertPrice(BigDecimal.valueOf(50000))
                .alertActive(true)
                .coinTicker(coinTicker)
                .email("user@example.com")
                .alertCondition("BELOW")
                .build();

        List<Alert> alerts = Collections.singletonList(alert);
        when(alertRepository.findByCoinTickerAndAlertActiveTrue(coinTicker)).thenReturn(alerts);

        // Redis에 캐시 저장 동작을 확인할 수 있도록 설정
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(null);

        // When: 가격이 48000으로 떨어졌을 때 메시지 수신
        CoinPriceDTO priceDTO = CoinPriceDTO.builder()
                .code("BTC")
                .price(6000.0)
                .changeRate(70.0)
                .acc_trade_price_24h(3.0)
                .build();
        String messagePayload = objectMapper.writeValueAsString(priceDTO);
        when(message.getBody()).thenReturn(messagePayload.getBytes(StandardCharsets.UTF_8));

        priceUpdateSubscriber.onMessage(message, null);

        // Then: 이메일 전송이 호출되었는지 확인
        verify(emailService, times(1)).sendEmailNotice(eq("user@example.com"), anyString(), anyString());

        // Redis에 알림 발송 기록이 저장되었는지 확인
//        verify(redisTemplate, atLeastOnce()).opsForValue();
//        verify(valueOperations, atLeastOnce()).get(cacheKey);
//
//        // Redis 캐시 삭제 검증 (1번 이상 호출될 수 있음)
//        verify(redisTemplate, atLeastOnce()).delete(eq("alert_list:BTC"));
//
//        // Redis에 알림 발송 기록이 저장되었는지 확인 (최대 1번만 저장)
//        String redisKey = "alert:null:BTC";
//        verify(valueOperations, atMost(1)).set(eq(redisKey), eq("sent"), eq(5L), eq(TimeUnit.MINUTES));

        // Alert가 비활성화되었는지 확인
        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    void onMessage_RecentAlertExists_NoEmailSent() throws Exception {
        // Given: 최근에 알림이 전송된 경우
        String coinTicker = "ETH";
        String redisKey = "alert:null:ETH";
        when(redisTemplate.hasKey(redisKey)).thenReturn(true);

        // When: 가격이 알림 조건을 충족하지만 Redis에 최근 발송 기록이 존재
        CoinPriceDTO priceDTO = CoinPriceDTO.builder()
                .code("ETH")
                .price(6000.0)
                .changeRate(70.0)
                .acc_trade_price_24h(3.0)
                .build();
        String messagePayload = objectMapper.writeValueAsString(priceDTO);
        when(message.getBody()).thenReturn(messagePayload.getBytes(StandardCharsets.UTF_8));

        priceUpdateSubscriber.onMessage(message, null);

        // Then: 이메일이 전송되지 않아야 함
        verify(emailService, never()).sendEmailNotice(anyString(), anyString(), anyString());
    }

    @Test
    void onMessage_AlertConditionNotMet_NoEmailSent() throws Exception {
        // Given: 알림이 있지만 조건을 충족하지 않는 경우
        String coinTicker = "XRP";
        when(redisTemplate.hasKey("alert_list:" + coinTicker)).thenReturn(false);

        Alert alert = Alert.builder()
                .alertPrice(BigDecimal.valueOf(1.5))
                .alertActive(true)
                .coinTicker(coinTicker)
                .email("user@example.com")
                .alertCondition("BELOW") // 가격이 내려갈 때만 알림
                .build();

        when(alertRepository.findByCoinTickerAndAlertActiveTrue(coinTicker)).thenReturn(Collections.singletonList(alert));

        // When: 가격이 1.8로 상승했을 때 메시지 수신
        CoinPriceDTO priceDTO = CoinPriceDTO.builder()
                .code("XRP")
                .price(6000.0)
                .changeRate(1.8)
                .acc_trade_price_24h(3.0)
                .build();
        String messagePayload = objectMapper.writeValueAsString(priceDTO);
        when(message.getBody()).thenReturn(messagePayload.getBytes(StandardCharsets.UTF_8));

        priceUpdateSubscriber.onMessage(message, null);

        // Then: 이메일이 전송되지 않아야 함
        verify(emailService, never()).sendEmailNotice(anyString(), anyString(), anyString());
    }
}