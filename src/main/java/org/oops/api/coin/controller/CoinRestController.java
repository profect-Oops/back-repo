package org.oops.api.coin.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.oops.api.coin.dto.CoinDTO;
import org.oops.api.coin.service.CoinService;
import org.oops.api.common.controller.BaseController;
import org.oops.api.common.dto.ResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
