package org.oops.global.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import okhttp3.*;
import okio.ByteString;
import org.oops.api.coin.dto.CoinCandleDTO;
import org.oops.api.coin.dto.CoinPriceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
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
    private WebSocket webSocket;
    private boolean shouldReconnect = true;

    @Autowired
    public UpbitWebSocketClient(Consumer<CoinPriceDTO> priceHandler, Consumer<CoinPriceDTO> priceDetailHandler, Consumer<CoinCandleDTO> candleHandler) {
        this.client = new OkHttpClient();
        this.priceDetailHandler = priceDetailHandler;
        this.priceHandler = priceHandler;
        this.candleHandler = candleHandler;
    }

    @PostConstruct
    public void connect() {
        logger.info("🚀 [UpbitWebSocketClient] connect() 실행됨");  // ✅ 실행 확인용 로그 추가
        Request request = new Request.Builder()
                .url(WEBSOCKET_URL)
                .build();

        webSocket = client.newWebSocket(request, new UpbitWebSocketListener(priceHandler, candleHandler, priceDetailHandler) {
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

@Component
class UpbitWebSocketListener extends WebSocketListener {
    private static final Logger logger = LoggerFactory.getLogger(UpbitWebSocketListener.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Consumer<CoinPriceDTO> priceHandler;
    private final Consumer<CoinCandleDTO> candleHandler;
    private final Consumer<CoinPriceDTO> priceDetailHandler;

    public UpbitWebSocketListener(Consumer<CoinPriceDTO> priceHandler, Consumer<CoinCandleDTO> candleHandler, Consumer<CoinPriceDTO> priceDetailHandler) {
        this.priceHandler = priceHandler;
        this.candleHandler = candleHandler;
        this.priceDetailHandler = priceDetailHandler;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        logger.info("WebSocket Opened");
        try {
            String message = getMessage();
            webSocket.send(message);
        } catch (IOException e) {
            logger.error("Error creating message", e);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        try {
            JsonNode jsonNode = objectMapper.readTree(bytes.utf8());

            if (!jsonNode.has("type")) {
                logger.warn("Received message without type field");
                return;
            }
            String type = jsonNode.get("type").asText();

            try {
                if (type.startsWith("candle")) {
                    CoinCandleDTO candle = processCandle(jsonNode);
                    candleHandler.accept(candle);
                } else if ("ticker".equals(type)) {
                    CoinPriceDTO price = processTicker(jsonNode);
                    priceHandler.accept(price);
                    priceDetailHandler.accept(price);
                }
            } catch (NullPointerException e) {
                logger.error("Missing required fields in message: {}", jsonNode.toString(), e);
            }
        } catch (IOException e) {
            logger.error("Error processing message: {}", bytes.utf8(), e);
        }
    }

    private CoinCandleDTO processCandle(JsonNode jsonNode) {
        return new CoinCandleDTO(
                jsonNode.get("code").asText(),
                jsonNode.get("opening_price").asDouble(),
                jsonNode.get("high_price").asDouble(),
                jsonNode.get("low_price").asDouble(),
                jsonNode.get("trade_price").asDouble(),
                jsonNode.get("candle_acc_trade_price").asDouble(),
                jsonNode.get("timestamp").asLong()
        );
    }

    private CoinPriceDTO processTicker(JsonNode jsonNode) {
        return new CoinPriceDTO(
                jsonNode.get("code").asText(),
                jsonNode.get("opening_price").asDouble(),
                jsonNode.get("signed_change_rate").asDouble(),
                jsonNode.get("acc_trade_price_24h").asDouble()
        );
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        logger.info("WebSocket Closed: {} / {}", code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        logger.error("WebSocket Error", t);
    }

    private String getMessage() throws IOException {
        List<Map<String, Object>> message = new ArrayList<>();

        // Ticket
        message.add(Collections.singletonMap("ticket", UUID.randomUUID().toString()));

        // Ticker
        Map<String, Object> ticker = new HashMap<>();
        ticker.put("type", "ticker");
        ticker.put("codes", Arrays.asList(UpbitWebSocketClient.COIN_CODES));
        message.add(ticker);

        // Candle
        Map<String, Object> candle = new HashMap<>();
        candle.put("type", "candle.1s");
        candle.put("codes", Arrays.asList(UpbitWebSocketClient.COIN_CODES));
        message.add(candle);

        // Format
        message.add(Collections.singletonMap("format", "DEFAULT"));

        return objectMapper.writeValueAsString(message);
    }
}
