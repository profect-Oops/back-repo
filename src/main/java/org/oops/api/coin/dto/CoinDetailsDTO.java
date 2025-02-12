package org.oops.api.coin.dto;

import lombok.Builder;
import lombok.Getter;
import org.oops.api.news.dto.NewsDTO;

import java.io.Serializable;
import java.util.List;

@Getter
public class CoinDetailsDTO implements Serializable {

    private CoinFindByNameDTO coin;
    private List<NewsDTO> newsList;

    @Builder
    public CoinDetailsDTO(CoinFindByNameDTO coin, List<NewsDTO> newsList) {
        this.coin = coin;
        this.newsList = newsList;
    }
}
