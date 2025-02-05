package org.oops.api.coin.dto;

import lombok.Builder;
import lombok.Getter;
import org.oops.domain.coin.Coin;

import javax.swing.text.html.parser.Entity;
import java.io.Serializable;

@Getter
public class CoinDTO implements Serializable {

    private final Long coinId;

    private final String name;

    private final Float prospects;

    private final String picture;

    private final Boolean isVisible;

    private final String ticker;

    @Builder
    public CoinDTO(Long coinId, String name, Float prospects, String picture, Boolean isVisible, String ticker){
        this.coinId = coinId;
        this.name = name;
        this.prospects = prospects;
        this.picture = picture;
        this.isVisible = isVisible;
        this.ticker = ticker;
    }

    public static CoinDTO fromEntity(Coin coin){
        return CoinDTO.builder()
                .coinId(coin.getCoinId())
                .name(coin.getName())
                .prospects(coin.getProspects())
                .picture(coin.getPicture())
                .isVisible(coin.getIsVisible())
                .ticker(coin.getTicker())
                .build();
    }
}
