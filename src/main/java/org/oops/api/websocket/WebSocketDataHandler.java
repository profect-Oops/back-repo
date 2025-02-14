package org.oops.api.websocket;

import org.oops.api.coin.dto.CoinCandleDTO;
import org.oops.api.coin.dto.CoinPriceDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

//데이터 전송
@Configuration  // ✅ Spring이 관리하는 Bean 등록
public class WebSocketDataHandler {
    private final WebSocketDataService webSocketDataService;

    public WebSocketDataHandler(WebSocketDataService webSocketDataService) {
        this.webSocketDataService = webSocketDataService;
    }

    @Bean
    public Consumer<CoinPriceDTO> priceHandler() {
        return webSocketDataService::sendPriceUpdate;
    }

    // ✅ 특정 마켓 가격 데이터를 처리하는 핸들러 (새로 추가됨)
    @Bean
    public Consumer<CoinPriceDTO> priceDetailHandler() {
        return webSocketDataService::sendPriceDetailUpdate;
    }

    // ✅ 캔들 데이터를 요청하는 핸들러 추가
    @Bean
    public Consumer<CoinCandleDTO> candleDetailHandler() {
        return webSocketDataService::sendCandleUpdate;
    }


}