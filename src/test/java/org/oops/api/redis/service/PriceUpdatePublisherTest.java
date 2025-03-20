package org.oops.api.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oops.api.coin.dto.CoinPriceDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceUpdatePublisherTest {

    @Mock
    private RedisTemplate<String, String> redisStringTemplate;

    @Mock
    private ChannelTopic priceUpdateTopic;

    private PriceUpdatePublisher priceUpdatePublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        priceUpdatePublisher = new PriceUpdatePublisher(redisStringTemplate, priceUpdateTopic);
    }

    @Test
    void publishPriceUpdate_Success() throws Exception {
        // Given: 코인 가격 DTO를 생성
        CoinPriceDTO priceDTO = CoinPriceDTO.builder()
                .code("BTC")
                .price(6000.0)
                .changeRate(70.0)
                .acc_trade_price_24h(3.0)
                .build();

        // When: PriceUpdatePublisher가 메시지를 발행
        priceUpdatePublisher.publishPriceUpdate(priceDTO);

        // Then: RedisTemplate의 convertAndSend 메서드가 호출되었는지 검증
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(redisStringTemplate, times(1)).convertAndSend(eq(priceUpdateTopic.getTopic()), messageCaptor.capture());

        // 메시지가 JSON 형태로 직렬화되었는지 확인
        String expectedMessage = objectMapper.writeValueAsString(priceDTO);
        assertEquals(expectedMessage, messageCaptor.getValue());
    }

    @Test
    void publishPriceUpdate_ExceptionHandling() throws Exception {
        // Given: 예외를 발생시키는 설정
        CoinPriceDTO priceDTO = CoinPriceDTO.builder()
                .code("BTC")
                .price(6000.0)
                .changeRate(70.0)
                .acc_trade_price_24h(3.0)
                .build();

        doThrow(new RuntimeException("Redis 에러 발생")).when(redisStringTemplate).convertAndSend(anyString(), anyString());

        // When & Then: 예외가 발생해도 프로그램이 중단되지 않고 로깅만 수행하는지 확인
        assertDoesNotThrow(() -> priceUpdatePublisher.publishPriceUpdate(priceDTO));

        // RedisTemplate이 호출되었는지 확인
        verify(redisStringTemplate, times(1)).convertAndSend(eq(priceUpdateTopic.getTopic()), anyString());
    }
}