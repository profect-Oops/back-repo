package org.oops.api.coin.dto;

import lombok.*;

import java.io.Serializable;

@Getter
public class CoinCandleDTO implements Serializable {
    private final String code;
    private final Double openingPrice;
    private final Double highPrice;
    private final Double lowPrice;
    private final Double tradePrice;
    private final Double candleAccTradePrice;
    private final Long timestamp;

    @Builder
    public CoinCandleDTO(String code, Double openingPrice, Double highPrice, Double lowPrice, Double tradePrice, Double candleAccTradePrice, Long timestamp) {
        this.code = code;
        this.openingPrice = openingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.tradePrice = tradePrice;
        this.candleAccTradePrice = candleAccTradePrice;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("CoinCandle{code='%s', openingPrice=%.2f, highPrice=%.2f, lowPrice=%.2f, tradePrice=%.2f, candleAccTradePrice=%.2f, timestamp=%d}",
                code, openingPrice, highPrice, lowPrice, tradePrice, candleAccTradePrice, timestamp);
    }

}
