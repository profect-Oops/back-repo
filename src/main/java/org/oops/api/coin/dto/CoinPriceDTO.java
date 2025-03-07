package org.oops.api.coin.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class CoinPriceDTO implements Serializable {
    private String code;
    private Double price;
    private Double changeRate;
    private Double acc_trade_price_24h;

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
