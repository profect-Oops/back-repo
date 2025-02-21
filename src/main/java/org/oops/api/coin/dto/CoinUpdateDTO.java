package org.oops.api.coin.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class CoinUpdateDTO {
    private String ticker;
    private String name;
    private String picture;

    @Builder
    public CoinUpdateDTO(String ticker, String name, String picture) {
        this.ticker = ticker;
        this.name = name;
        this.picture = picture;
    }
}
