package org.oops.api.coin.service;

import org.oops.api.coin.dto.CoinDTO;
import org.oops.api.coin.dto.CoinFindByNameDTO;
import org.oops.api.coin.dto.CreateCoinDTO;

import java.util.List;

/**
 * CoinService는 코인과 관련된 다양한 기능을 제공합니다.
 *
 * @author: MJLee39
 */
public interface CoinService {

    /**
     * coin 모두 조회
     */
    List<CoinDTO> getAllCoins();

    /**
     * 한 코인의 정보를 보여줍니다.
     *
     * @return 코인DTO 응답
     */
    CoinDTO getCoinById(Long coinId);

    /**
     * 코인 이름으로 코인 찾기
     *
     * @return CoinFindByNameDTO
     */
    CoinFindByNameDTO findCoinIdByCoinName(String coinName);

    /**
     * 코인 이름으로 코인 찾아서 coinDTO 반환
     */
    CoinDTO findCoinByCoinName(String coinName);

    /**
     * name이 존재하는지 확인하고 없으면 코인을 Insert 하는 로직
     */
    void insertCoin(String name, String ticker, String picture);

    /**
     * 코인 정보가 DB에 존재하는지 확인 후, 없으면 저장
     */
    List<CreateCoinDTO> addCoinsToDatabase(List<CreateCoinDTO> coinInfoArray);

}
