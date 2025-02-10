package org.oops.api.coin.service;

import org.oops.api.coin.dto.CoinDTO;
import org.oops.api.coin.dto.CoinFindByNameDTO;

import java.util.List;

/**
 * CoinService는 코인과 관련된 다양한 기능을 제공합니다.
 *
 * @author: MJLee39
 */
public interface CoinService {
    /**
     * 거래량 순위 10위 안의 코인을 리스트 형태로 모두 보여줍니다.
     *
     * @return 코인DTO 리스트 응답
     */
    List<CoinDTO> getVisibleCoins();

    /**
     * 한 코인의 정보를 보여줍니다.
     *
     * @return 코인DTO 응답
     */
    CoinDTO getCoinById(Long coinId);

    /**
     * 코인 이름으로 코인 찾기
     *
     * @return 코인DTO
     */
    CoinFindByNameDTO findCoinByCoinName(String coinName);
}
