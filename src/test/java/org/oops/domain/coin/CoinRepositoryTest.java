package org.oops.domain.coin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoinRepositoryTest {

    @Mock
    private CoinRepository coinRepository;

    @Test
    @DisplayName("이름으로 코인 조회")
    void findByName() {
        //given
        BigDecimal bigDecimal = new BigDecimal("0.03");
        Coin coin = new Coin("비트코인", bigDecimal, "http://localhost", "BTC", "지피티 정보");
        when(coinRepository.findByName("비트코인")).thenReturn(Optional.of(coin));

        //when
        Optional<Coin> result = coinRepository.findByName("비트코인");

        //then
        assertThat(result).isPresent();  // 값이 존재해야 함
        assertThat(result.get().getTicker()).isEqualTo("BTC");

    }

    @Test
    @DisplayName("티커로 코인 조회")
    void findByTicker() {
        //given
        BigDecimal bigDecimal = new BigDecimal("0.03");
        Coin coin = new Coin("비트코인", bigDecimal, "http://localhost", "BTC", "지피티 정보");
        when(coinRepository.findByTicker("BTC")).thenReturn(Optional.of(coin));

        //when
        Optional<Coin> result = coinRepository.findByTicker("BTC");

        //then
        assertThat(result).isPresent();  // 값이 존재해야 함
        assertThat(result.get().getName()).isEqualTo("비트코인");
    }

    @Test
    @DisplayName("존재하지 않는 코인 이름 조회")
    void findByNonExistingName() {
        // when
        Optional<Coin> result = coinRepository.findByName("이더리움");

        // then
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("존재하지 않는 티커 조회")
    void findByNonExistingTicker() {
        // when
        Optional<Coin> result = coinRepository.findByTicker("ETH");

        // then
        assertThat(result).isNotPresent();
    }
}