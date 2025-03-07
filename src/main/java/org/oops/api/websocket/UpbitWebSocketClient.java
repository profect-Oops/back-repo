package org.oops.api.websocket;


import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.oops.api.coin.dto.CoinCandleDTO;
import org.oops.api.coin.dto.CoinPriceDTO;
import org.oops.api.coin.service.CoinPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;

@Component
public class UpbitWebSocketClient {
    public static final String[] COIN_CODES = {
            "KRW-XRP", "KRW-BTC", "KRW-ETH", "KRW-QTUM",
            "KRW-WAVES", "KRW-XEM", "KRW-ETC", "KRW-NEO",
            "KRW-SNT", "KRW-MTL"
    };
    private static final Logger logger = LoggerFactory.getLogger(UpbitWebSocketClient.class);
    private static final String WEBSOCKET_URL = "wss://api.upbit.com/websocket/v1";
    private static final int RECONNECT_DELAY = 5000; // 5초

    private final OkHttpClient client;
    private final Consumer<CoinPriceDTO> priceHandler;
    private final Consumer<CoinCandleDTO> candleHandler;
    private final Consumer<CoinPriceDTO> priceDetailHandler;
    private final CoinPriceService coinPriceService;

    private WebSocket webSocket;
    private boolean shouldReconnect = true;

    @Autowired
    public UpbitWebSocketClient(Consumer<CoinPriceDTO> priceHandler, Consumer<CoinPriceDTO> priceDetailHandler, Consumer<CoinCandleDTO> candleHandler, CoinPriceService coinPriceService) {
        this.client = new OkHttpClient();
        this.priceDetailHandler = priceDetailHandler;
        this.priceHandler = priceHandler;
        this.candleHandler = candleHandler;
        this.coinPriceService = coinPriceService;
    }

    @PostConstruct
    public void connect() {
        logger.info("🚀 [UpbitWebSocketClient] connect() 실행됨");  // ✅ 실행 확인용 로그 추가
        Request request = new Request.Builder()
                .url(WEBSOCKET_URL)
                .build();

        webSocket = client.newWebSocket(request, new UpbitWebSocketListener(priceHandler, candleHandler, priceDetailHandler, coinPriceService) {
            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                logger.warn("⚠️ [UpbitWebSocketClient] WebSocket 연결 종료됨. 이유: {}", reason);
                reconnect();
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                logger.error("❌ [UpbitWebSocketClient] WebSocket 연결 실패: ", t);
                reconnect();
            }
        });
    }

    private void reconnect() {
        if (!shouldReconnect) return;

        try {
            Thread.sleep(RECONNECT_DELAY);
            logger.info("Attempting to reconnect...");
            connect();
        } catch (InterruptedException e) {
            logger.error("Reconnection interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    public void close() {
        shouldReconnect = false;
        if (webSocket != null) {
            webSocket.close(1000, "Closing connection");
            client.dispatcher().executorService().shutdown();
        }
    }
}