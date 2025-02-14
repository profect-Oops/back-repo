package org.oops.api.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okio.ByteString;
import org.oops.api.coin.dto.CoinCandleDTO;
import org.oops.api.coin.dto.CoinPriceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

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
