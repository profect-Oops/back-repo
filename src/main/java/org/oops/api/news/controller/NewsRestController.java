package org.oops.api.news.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.oops.api.common.controller.BaseController;
import org.oops.api.common.dto.ResponseDTO;
import org.oops.api.news.dto.NewsDTO;
import org.oops.api.news.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/news")
public class NewsRestController extends BaseController {

    private final NewsService newsService;

    @GetMapping(value = "/read/{coinId}")
    public ResponseDTO<List<NewsDTO>> getNewsByCoinId(@PathVariable("coinId") Long coinId){
        return ResponseDTO.ok(newsService.getNewsByCoinId(coinId));
    }
}
