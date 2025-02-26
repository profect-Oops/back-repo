package org.oops.api.coin.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oops.api.coin.dto.*;
import org.oops.api.coin.service.CoinService;
import org.oops.api.common.controller.BaseController;
import org.oops.api.common.dto.ResponseDTO;
import org.oops.api.news.dto.NewsDTO;
import org.oops.api.news.service.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/coin")
public class CoinRestController extends BaseController {

    private final CoinService coinService;
    private final NewsService newsService;

    @GetMapping("/list")
    public ResponseEntity<List<CoinDTO>> getAllCoins() {
        List<CoinDTO> coins = coinService.getAllCoins();
        return ResponseEntity.ok(coins);
    }

    @GetMapping(value = "/read/{coinId}")
    public ResponseDTO<CoinDTO> getCoin(@PathVariable Long coinId){
        return ResponseDTO.ok(coinService.getCoinById(coinId));
    }

    //코인 이름으로 코인 디테일 정보 확인
    @GetMapping("/details/{name}")
    public ResponseEntity<?> getCoinDetails(@PathVariable String name) {
        String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8); // ✅ 한글 URL 디코딩
        CoinDTO coin = coinService.findCoinByCoinName(decodedName);

        // 코인 정보를 name으로 조회
        //CoinDTO coin = coinService.findCoinByCoinName(name);

        if (coin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("코인을 찾을 수 없습니다.");
        }

        // 관련 뉴스 조회
        List<NewsDTO> newsList = newsService.getNewsByCoinId(coin.getCoinId());

        // DTO로 감싸서 반환
        CoinDetailsDTO response = CoinDetailsDTO.builder()
                .coin(coin)
                .newsList(newsList)
                .build();

        return ResponseEntity.ok(response);
    }

    //코인 추가
    @PostMapping("/add")
    public List<CreateCoinDTO> addCoinsToServer(@RequestBody List<CreateCoinDTO> coinInfoArray) {
        return coinService.addCoinsToDatabase(coinInfoArray); // 코인 정보를 DB에 추가
    }

    //코인 수정 - 코인 이름, 사진만 수정
    @PutMapping("/update")
    public ResponseEntity<ResponseDTO<String>> updateCoinInfo(@RequestBody List<CoinUpdateDTO> coinUpdateList) {
        boolean updated = coinService.updateCoinInfo(coinUpdateList);
        if (updated) {
            return ResponseEntity.ok(ResponseDTO.ok("✅ 코인 정보 업데이트 완료."));
        } else {
            return ResponseEntity.badRequest().body(ResponseDTO.error("🚨 업데이트할 코인이 없습니다."));
        }
    }

}
