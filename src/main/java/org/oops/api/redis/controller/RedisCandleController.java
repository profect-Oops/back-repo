package org.oops.api.redis.controller;

import org.oops.api.coin.dto.CoinCandleDTO;
import org.oops.api.redis.service.RedisCandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/redis/candles")
public class RedisCandleController {

    private final RedisCandleService redisCandleService;

    @Autowired
    public RedisCandleController(RedisCandleService redisCandleService) {
        this.redisCandleService = redisCandleService;
    }

    // 특정 마켓의 과거 캔들 데이터 조회 (캐싱 포함)
    @GetMapping("/{market}")
    public List<CoinCandleDTO> getHistoricalCandles(@PathVariable String market, @RequestParam(value = "to", required = false) String to) {
        return redisCandleService.getHistoricalCandles(market, to);
    }
}