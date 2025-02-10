package org.oops.api.coin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.oops.domain.coin.Coin;

@Getter
@AllArgsConstructor
public class CoinFindByNameDTO {
    private Long id;
    private String name;

    public static CoinFindByNameDTO fromEntity(Coin coin) {
        return new CoinFindByNameDTO(coin.getCoinId(), coin.getName());
    }
}

