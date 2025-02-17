package org.oops.api.coin.controller;

import lombok.RequiredArgsConstructor;
import org.oops.api.coin.dto.CoinDTO;
import org.oops.api.coin.dto.CoinFindByNameDTO;
import org.oops.api.coin.service.CoinService;
import org.oops.api.common.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/coin")
public class CoinController extends BaseController {

    private final CoinService coinService;

    @GetMapping(value = "/read/{coinId}")
    public String getCoin(@PathVariable Long coinId, Model model) {
        CoinDTO coin = coinService.getCoinById(coinId);
        model.addAttribute("coin", coin);
        return "coin/detail";
    }
}
