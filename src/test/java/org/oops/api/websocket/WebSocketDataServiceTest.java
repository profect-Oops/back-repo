package org.oops.api.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oops.api.coin.dto.CoinCandleDTO;
import org.oops.api.coin.dto.CoinPriceDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebSocketDataServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketDataService webSocketDataService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("가격 전체 데이터 WebSocket 전송 테스트")
    void testSendPriceUpdate() {
        // Given
        CoinPriceDTO price = new CoinPriceDTO("KRW-BTC", 50000.0, 0.05, 1000000.0);

        // When
        webSocketDataService.sendPriceUpdate(price);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend("/topic/price", price);
    }

    @Test
    @DisplayName("특정 마켓 가격 업데이트 WebSocket 전송 테스트")
    void testSendPriceDetailUpdate_ValidData() {
        // Given
        CoinPriceDTO price = new CoinPriceDTO("KRW-BTC", 50000.0, 0.05, 1000000.0);
        String expectedDestination = "/topic/priceDetail/KRW-BTC";

        // When
        webSocketDataService.sendPriceDetailUpdate(price);

        // Then
        ArgumentCaptor<CoinPriceDTO> captor = ArgumentCaptor.forClass(CoinPriceDTO.class);
        verify(messagingTemplate, times(1)).convertAndSend(eq(expectedDestination), captor.capture());

        assertEquals(price, captor.getValue());
    }

    @Test
    @DisplayName("특정 마켓 가격 업데이트 - 유효하지 않은 데이터 처리")
    void testSendPriceDetailUpdate_InvalidData() {
        // Given
        CoinPriceDTO invalidPrice = CoinPriceDTO.builder()
                .code(null)
                .price(0.0)
                .acc_trade_price_24h(0.0)
                .changeRate(0.0)
                .build();

        // When
        webSocketDataService.sendPriceDetailUpdate(invalidPrice);

        // Then
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    @DisplayName("캔들 업데이트 WebSocket 전송 테스트")
    void testSendCandleUpdate_ValidData() {
        // Given
        CoinCandleDTO candle = new CoinCandleDTO("KRW-ETH", 40000.0, 42000.0, 38000.0, 41000.0, 50000.0, System.currentTimeMillis());
        String expectedDestination = "/topic/candle/KRW-ETH";

        // When
        webSocketDataService.sendCandleUpdate(candle);

        // Then
        ArgumentCaptor<CoinCandleDTO> captor = ArgumentCaptor.forClass(CoinCandleDTO.class);
        verify(messagingTemplate, times(1)).convertAndSend(eq(expectedDestination), captor.capture());

        assertEquals(candle, captor.getValue());
    }

    @Test
    @DisplayName("캔들 업데이트 - 유효하지 않은 데이터 처리")
    void testSendCandleUpdate_InvalidData() {
        // Given
        CoinCandleDTO invalidCandle = new CoinCandleDTO(null, 0.0, 0.0, 0.0, 0.0, 0.0, 0L);

        // When
        webSocketDataService.sendCandleUpdate(invalidCandle);

        // Then
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }
}