package org.oops.api.alert.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.oops.api.alert.dto.GetAlertResponseDTO;
import org.oops.api.alert.service.AlertService;
import org.oops.api.common.controller.BaseController;
import org.oops.global.config.auth.dto.SessionUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/alert")
public class AlertController extends BaseController {
    private final AlertService alertService;

    // 내 알림 리스트 조회
    @GetMapping(value = "/read")
    public String getAlert(HttpSession session, Model model) {
        SessionUser principal = (SessionUser) session.getAttribute("user");
        List<GetAlertResponseDTO> alerts = alertService.getAlertByUserId(principal.getId());
        model.addAttribute("alerts", alerts); // coin 정보가 포함된 alerts 리스트 전달
        return "alert/alarm"; // alert/alarm.html 뷰 반환
    }
}
