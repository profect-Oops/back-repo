package org.oops.api.redis.service;

import org.oops.api.coin.dto.CoinCandleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCandleService {

    private static final Logger logger = LoggerFactory.getLogger(RedisCandleService.class);
    private final RestClient restClient;
    private final RedisTemplate<String, List<CoinCandleDTO>> redisTemplate;  // 제네릭 타입 지정

    public RedisCandleService(RestClient restClient, RedisTemplate<String, List<CoinCandleDTO>> redisTemplate) {
        this.restClient = restClient;
        this.redisTemplate = redisTemplate;
    }

    // 과거 캔들 데이터를 Redis에 캐싱 (market별로 저장됨)
    public List<CoinCandleDTO> getHistoricalCandles(String market, String to) {
        String redisKey = "candles:" + market;

        // to 값이 있으면 캐시 키에 추가 (to 값 기준으로 다른 데이터를 캐싱)
        if (to != null) {
            redisKey += ":to:" + to;
        }

        // Redis에서 먼저 조회
        List<CoinCandleDTO> cachedData = (List<CoinCandleDTO>) redisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            logger.info("📦 Redis 캐시에서 데이터 가져옴: {}", redisKey);
            return cachedData;
        }

        // 캐시가 없으면 Upbit API 호출
        String url = "https://api.upbit.com/v1/candles/minutes/1?market=" + market + "&count=60";
        if (to != null) {
            url += "&to=" + to;  // ✅ `to` 값을 API 요청에 포함
        }

        logger.info("📡 외부 API 요청: {}", url);

        CoinCandleDTO[] response = restClient.get()
                .uri(url)
                .retrieve()
                .body(CoinCandleDTO[].class);

        List<CoinCandleDTO> candles = response != null ? Arrays.asList(response) : List.of();

        // Redis에 저장 (TTL: 10분)
        redisTemplate.opsForValue().set(redisKey, candles, 10, TimeUnit.MINUTES);
        logger.info("Redis에 저장 완료: {}", redisKey);

        return candles;
    }
}
