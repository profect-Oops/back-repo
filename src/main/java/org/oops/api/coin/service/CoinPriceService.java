package org.oops.api.coin.service;

import org.oops.api.coin.dto.CoinPriceDTO;
import org.oops.api.redis.service.PriceUpdatePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CoinPriceService {
    private static final Logger logger = LoggerFactory.getLogger(CoinPriceService.class);
    private final PriceUpdatePublisher priceUpdatePublisher;

    public CoinPriceService(PriceUpdatePublisher priceUpdatePublisher) {
        this.priceUpdatePublisher = priceUpdatePublisher;
    }

    // 코인 가격 업데이트 시 Redis Pub/Sub 전송
    public void updateCoinPrice(String ticker, double price) {
        CoinPriceDTO priceDTO = CoinPriceDTO.builder()
                .code(ticker)
                .price(price)
                .build();
        priceUpdatePublisher.publishPriceUpdate(priceDTO);
    }
}