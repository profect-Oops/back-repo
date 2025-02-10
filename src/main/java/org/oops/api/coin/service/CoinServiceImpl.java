package org.oops.api.coin.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oops.api.coin.dto.CoinDTO;
import org.oops.api.coin.dto.CoinFindByNameDTO;
import org.oops.api.coin.dto.CreateCoinDTO;
import org.oops.domain.coin.Coin;
import org.oops.domain.coin.CoinRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CoinServiceImpl implements CoinService {

    private final CoinRepository coinRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CoinDTO> getVisibleCoins(){
        return coinRepository.findByIsVisibleTrue()
                .stream()
                .map(CoinDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CoinDTO getCoinById(Long coinId){
        return coinRepository.findById(coinId)
                .map(coin -> new CoinDTO(coin.getCoinId(), coin.getName(), coin.getProspects(), coin.getPicture(), coin.getIsVisible(), coin.getTicker()))
                .orElseThrow(() -> new EntityNotFoundException("해당 코인을 찾을 수 없습니다. ID: " + coinId));
    }

    //코인 이름으로 코인 조회
    @Override
    @Transactional(readOnly = true)
    public CoinFindByNameDTO findCoinByCoinName(String coinName){
        return coinRepository.findByName(coinName)
                .map(coin -> new CoinFindByNameDTO(coin.getCoinId(), coin.getName()))
                .orElseThrow(() -> new EntityNotFoundException("해당 코인을 찾을 수 없습니다. coinName: " + coinName));
    }

    // 코인 추가
    @Override
    @Transactional
    public void insertCoin(String name, String ticker, String picture){
        // 티커가 이미 존재하는지 확인
        Optional<Coin> existingCoin = coinRepository.findByTicker(ticker);

        if (existingCoin.isPresent()) {
            System.out.println("티커 '" + ticker + "'는 이미 존재합니다.");
        } else {
            Coin newCoin = Coin.builder()
                    .name(name)
                    .ticker(ticker)
                    .picture(picture)
                    .build();
            coinRepository.save(newCoin);
            System.out.println("새로운 코인 '" + name + "' (티커: " + ticker + ")를 추가했습니다.");
        }

    }

    // 코인 정보가 DB에 존재하는지 확인 후, 없으면 저장
    public List<CreateCoinDTO> addCoinsToDatabase(List<CreateCoinDTO> coinInfoArray) {
        List<CreateCoinDTO> savedCoins = new ArrayList<>();

        for (CreateCoinDTO coinDTO : coinInfoArray) {
            // DB에서 해당 티커의 코인이 존재하는지 확인
            Optional<Coin> existingCoin = coinRepository.findByTicker(coinDTO.getTicker());
            if (existingCoin.isEmpty()) {
                // 코인이 없다면 새로운 코인으로 저장
                Coin coin = Coin.builder()
                        .name(coinDTO.getName())
                        .ticker(coinDTO.getTicker())
                        .picture(coinDTO.getPicture())
                        .build();
                coinRepository.save(coin);
                savedCoins.add(coinDTO); // 저장된 코인 정보 추가
            }
        }

        return savedCoins;
    }
}
