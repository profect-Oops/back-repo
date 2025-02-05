package org.oops.api.news.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oops.api.coin.dto.CoinDTO;
import org.oops.api.common.exception.BaseException;
import org.oops.api.common.exception.NotFoundNewsException;
import org.oops.api.news.dto.NewsDTO;
import org.oops.domain.news.News;
import org.oops.domain.news.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NewsDTO> getNewsByCoinId(Long coinId){
        List<News> list = newsRepository.findByCoinId_CoinId(coinId);

        if(list == null && list.isEmpty()){
            throw new NotFoundNewsException("해당 코인의 뉴스가 없습니다.");
        }

        return newsRepository.findByCoinId_CoinId(coinId)
                .stream()
                .map(NewsDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
