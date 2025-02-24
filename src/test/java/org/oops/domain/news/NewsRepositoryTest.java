package org.oops.domain.news;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewsRepositoryTest {

    @Mock
    private NewsRepository newsRepository;

    @Test
    @DisplayName("코인 아이디로 뉴스 조회")
    void findByCoinId_CoinId() {
    }
}