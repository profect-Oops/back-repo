package org.oops.api.coin.dto;

import lombok.Builder;
import lombok.Getter;
import org.oops.domain.coin.Coin;

import javax.swing.text.html.parser.Entity;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
public class CoinDTO implements Serializable {

    private final Long coinId;

    private final String name;

    private final BigDecimal prospects;

    private final String picture;

    private final Boolean isVisible;

    private final String ticker;

    private final String gptData;

    @Builder
    public CoinDTO(Long coinId, String name, BigDecimal prospects, String picture, Boolean isVisible, String ticker, String gptData){
        this.coinId = coinId;
        this.name = name;
        this.prospects = prospects;
        this.picture = picture;
        this.isVisible = isVisible;
        this.ticker = ticker;
        this.gptData = gptData;
    }

    public static CoinDTO fromEntity(Coin coin){
        return CoinDTO.builder()
                .coinId(coin.getCoinId())
                .name(coin.getName())
                .prospects(coin.getProspects())
                .picture(coin.getPicture())
                .isVisible(coin.getIsVisible())
                .ticker(coin.getTicker())
                .gptData(coin.getGptData())
                .build();
    }
}
