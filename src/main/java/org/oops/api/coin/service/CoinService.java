package org.oops.api.coin.service;

import org.oops.api.coin.dto.CoinDTO;

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
}
