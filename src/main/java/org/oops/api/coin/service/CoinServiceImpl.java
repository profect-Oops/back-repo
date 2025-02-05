package org.oops.api.coin.service;

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
                .map(CoinDTO::fromEnity)
                .collect(Collectors.toList());
    }

}
