package org.oops.api.coin.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oops.api.coin.dto.CoinDTO;
import org.oops.domain.coin.CoinRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

}
