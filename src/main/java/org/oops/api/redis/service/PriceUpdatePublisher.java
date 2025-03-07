package org.oops.api.redis.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.oops.api.coin.dto.CoinPriceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class PriceUpdatePublisher {
    private static final Logger logger = LoggerFactory.getLogger(PriceUpdatePublisher.class);
    private final RedisTemplate<String, String> redisStringTemplate;
    private final ChannelTopic priceUpdateTopic;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PriceUpdatePublisher(RedisTemplate<String, String> redisStringTemplate, ChannelTopic priceUpdateTopic) {
        this.redisStringTemplate = redisStringTemplate;
        this.priceUpdateTopic = priceUpdateTopic;
    }

    public void publishPriceUpdate(CoinPriceDTO priceDTO) {
        try {
            String message = objectMapper.writeValueAsString(priceDTO);
            redisStringTemplate.convertAndSend(priceUpdateTopic.getTopic(), message);
        } catch (Exception e) {
            logger.error("❌ Redis Pub/Sub 메시지 전송 실패", e);
        }
    }
}
