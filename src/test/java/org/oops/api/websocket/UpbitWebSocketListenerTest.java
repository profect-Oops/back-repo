package org.oops.api.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okio.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.oops.api.coin.dto.CoinCandleDTO;
import org.oops.api.coin.dto.CoinPriceDTO;
import org.oops.api.coin.service.CoinPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpbitWebSocketListenerTest {

    private static final Logger logger = LoggerFactory.getLogger(UpbitWebSocketListenerTest.class);

    private UpbitWebSocketListener listener;
    private WebSocket mockWebSocket;
    private Consumer<CoinPriceDTO> priceHandler;
    private Consumer<CoinPriceDTO> priceDetailHandler;
    private Consumer<CoinCandleDTO> candleHandler;
    private CoinPriceService coinPriceService;

    @BeforeEach
    void setUp() {
        priceHandler = mock(Consumer.class);
        priceDetailHandler = mock(Consumer.class);
        candleHandler = mock(Consumer.class);
        coinPriceService = mock(CoinPriceService.class);
        listener = new UpbitWebSocketListener(priceHandler, candleHandler, priceDetailHandler, coinPriceService);

        mockWebSocket = mock(WebSocket.class);
    }


    @Test
    void testOnOpen_ShouldSendSubscriptionMessage() throws Exception {
        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("https://api.upbit.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(101) // Switching Protocols (WebSocket 성공)
                .message("Switching Protocols")
                .body(ResponseBody.create("", null))
                .build();

        listener.onOpen(mockWebSocket, mockResponse);

        verify(mockWebSocket, times(1)).send(anyString());
    }

    @Test
    void testOnMessage_ShouldProcessTickerMessage() throws IOException {
        // given
        String jsonMessage = "{ \"type\": \"ticker\", \"code\": \"KRW-BTC\", \"trade_price\": 75000000, \"signed_change_rate\": 0.01, \"acc_trade_price_24h\": 1000000000 }";
        ByteString byteString = ByteString.encodeUtf8(jsonMessage);

        // when
        listener.onMessage(mockWebSocket, byteString);

        // then
        ArgumentCaptor<CoinPriceDTO> captor = ArgumentCaptor.forClass(CoinPriceDTO.class);
        verify(priceHandler, times(1)).accept(captor.capture());
        verify(priceDetailHandler, times(1)).accept(captor.capture());
        verify(coinPriceService, times(1)).updateCoinPrice(eq("KRW-BTC"), eq(75000000.0));

        CoinPriceDTO received = captor.getValue();
        assertEquals("KRW-BTC", received.getCode());
        assertEquals(75000000.0, received.getPrice());
        assertEquals(0.01, received.getChangeRate());

    }

    @Test
    void testOnMessage_ShouldProcessCandleMessage() throws IOException {
        // given
        String jsonMessage = "{ \"type\": \"candle.1s\", \"code\": \"KRW-ETH\", \"opening_price\": 3000000, \"high_price\": 3100000, \"low_price\": 2950000, \"trade_price\": 3050000, \"candle_acc_trade_price\": 500000000, \"timestamp\": 1625247600000 }";
        ByteString byteString = ByteString.encodeUtf8(jsonMessage);

        // when
        listener.onMessage(mockWebSocket, byteString);

        // then
        ArgumentCaptor<CoinCandleDTO> captor = ArgumentCaptor.forClass(CoinCandleDTO.class);
        verify(candleHandler, times(1)).accept(captor.capture());

        CoinCandleDTO received = captor.getValue();
        assertEquals("KRW-ETH", received.getCode());
        assertEquals(3000000.0, received.getOpeningPrice());
        assertEquals(3100000.0, received.getHighPrice());
        assertEquals(2950000.0, received.getLowPrice());
        assertEquals(3050000.0, received.getTradePrice());
        assertEquals(500000000.0, received.getCandleAccTradePrice());
        assertEquals(1625247600000L, received.getTimestamp());
    }

    @Test
    void testOnClosed_ShouldLogClosure() {
        // when
        listener.onClosed(mockWebSocket, 1000, "Normal closure");

        // then
        logger.info("WebSocket Closed: 1000 / Normal closure");
        verifyNoInteractions(priceHandler, candleHandler, priceDetailHandler, coinPriceService);
    }

}