package org.oops.api.news.service;

import org.oops.api.news.dto.NewsDTO;

import java.util.List;

/**
 * CoinService는 뉴스와 관련된 다양한 기능을 제공합니다.
 *
 * @author: MJLee39
 */
public interface NewsService {
    /**
     * 코인ID 별 뉴스를 리스트 형태로 모두 보여줍니다.
     *
     * @return 뉴스DTO 리스트 응답
     */
    List<NewsDTO> getNewsByCoinId(Long coinId);
}
