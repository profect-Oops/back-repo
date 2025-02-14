package org.oops.api.coin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
public class CoinPriceDTO implements Serializable {
    private final String code;
    private final Double price;
    private final Double changeRate;
    private final Double acc_trade_price_24h;

    @Builder
    public CoinPriceDTO(String code, Double price, Double changeRate, Double acc_trade_price_24h) {
        this.code = code;
        this.price = price;
        this.changeRate = changeRate;
        this.acc_trade_price_24h = acc_trade_price_24h;
    }

    @Override
    public String toString() {
        return String.format("CoinPrice{code='%s', price=%.2f, changeRate=%.2f, acc_trade_price_24h=%.2f}", code, price, changeRate, acc_trade_price_24h);
    }
}
