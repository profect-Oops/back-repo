package org.oops.api.websocket;

import org.oops.api.coin.dto.CoinPriceDTO;
import org.oops.api.coin.dto.CoinCandleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.oops.domain.alert.Alert;

//데이터 전송
@Service
public class WebSocketDataService {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketDataService.class);
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketDataService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendPriceUpdate(CoinPriceDTO price) {
        //logger.info("가격 전체 데이터 전송: {}", price);
        messagingTemplate.convertAndSend("/topic/price", price);
    }


    // 특정 마켓의 가격 업데이트 전송
    public void sendPriceDetailUpdate(CoinPriceDTO price) {
        if (price == null || price.getCode() == null) {
            logger.warn("[WebSocketDataService] 유효하지 않은 가격 데이터");
            return;
        }
        String destination = "/topic/priceDetail/" + price.getCode();
        //logger.info("가격 데이터 전송: {} → {}", price, destination);
        messagingTemplate.convertAndSend(destination, price);
    }

    // 캔들 업데이트 전송
    public void sendCandleUpdate(CoinCandleDTO candle) {
        if (candle == null || candle.getCode() == null) {
            logger.warn("⛔ [WebSocketDataService] 유효하지 않은 캔들 데이터");
            return;
        }
        String destination = "/topic/candle/" + candle.getCode();
        //logger.info("📤 캔들 데이터 전송: {} → {}", candle, destination);
        messagingTemplate.convertAndSend(destination, candle);
    }

}

