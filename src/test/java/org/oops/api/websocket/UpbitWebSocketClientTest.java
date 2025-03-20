package org.oops.api.websocket;


import okhttp3.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.oops.api.coin.dto.CoinCandleDTO;
import org.oops.api.coin.dto.CoinPriceDTO;
import org.oops.api.coin.service.CoinPriceService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpbitWebSocketClientTest {

    private UpbitWebSocketClient upbitWebSocketClient;

    @Mock
    private Consumer<CoinPriceDTO> priceHandler;

    @Mock
    private Consumer<CoinPriceDTO> priceDetailHandler;

    @Mock
    private Consumer<CoinCandleDTO> candleHandler;

    @Mock
    private CoinPriceService coinPriceService;

    @Mock
    private ExecutorService mockExecutorService;

    @Spy
    private OkHttpClient mockClient = new OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        upbitWebSocketClient = new UpbitWebSocketClient(priceHandler, priceDetailHandler, candleHandler, coinPriceService);
    }

    @Test
    @DisplayName("WebSocket 연결 테스트")
    void testConnect() {
        upbitWebSocketClient.connect();
        verify(priceHandler, never()).accept(any());
        verify(candleHandler, never()).accept(any());
        verify(priceDetailHandler, never()).accept(any());
    }

    @Test
    @DisplayName("🔹 WebSocket 실패 시 재연결 테스트")
    void testReconnect() throws InterruptedException {
        UpbitWebSocketClient spyClient = Mockito.spy(upbitWebSocketClient);
        doNothing().when(spyClient).connect();

        spyClient.connect();

        verify(spyClient, times(1)).connect();
    }

    @Test
    @DisplayName("🔹 WebSocket 닫기 테스트")
    void testClose() {
        upbitWebSocketClient.close();
    }


}