package org.oops.api.coin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.oops.domain.coin.Coin;

@Getter
public class CreateCoinDTO {
    private String name;
    private String ticker;
    private String picture;

    @Builder
    public CreateCoinDTO(String name, String ticker, String picture) {
        this.name = name;
        this.ticker = ticker;
        this.picture = picture;
    }

    public static CreateCoinDTO fromEntity(Coin coin){
        return CreateCoinDTO.builder()
                .name(coin.getName())
                .picture(coin.getPicture())
                .ticker(coin.getTicker())
                .build();
    }
}
