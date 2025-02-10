package org.oops.api.coin.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.oops.api.coin.dto.CoinDTO;
import org.oops.api.coin.dto.CoinFindByNameDTO;
import org.oops.api.coin.dto.CreateCoinDTO;
import org.oops.api.coin.service.CoinService;
import org.oops.api.common.controller.BaseController;
import org.oops.api.common.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/coin")
public class CoinRestController extends BaseController {

    private final CoinService coinService;

    @GetMapping(value = "/read")
    public ResponseDTO<List<CoinDTO>> getCoinsList(){
        return ResponseDTO.ok(coinService.getVisibleCoins());
    }

    @GetMapping(value = "/read/{coinId}")
    public ResponseDTO<CoinDTO> getCoin(@PathVariable Long coinId){
        return ResponseDTO.ok(coinService.getCoinById(coinId));
    }

    //코인 이름으로 조회
    @GetMapping("/details/{name}")
    public ResponseEntity<?> getCoinDetails(@PathVariable String name) {
        // 코인 정보를 name으로 조회
        CoinFindByNameDTO coin = coinService.findCoinByCoinName(name);

        if (coin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("코인을 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(coin);
    }

    //코인 추가
    @PostMapping("/add")
    public List<CreateCoinDTO> addCoinsToServer(@RequestBody List<CreateCoinDTO> coinInfoArray) {
        return coinService.addCoinsToDatabase(coinInfoArray); // 코인 정보를 DB에 추가
    }

}
