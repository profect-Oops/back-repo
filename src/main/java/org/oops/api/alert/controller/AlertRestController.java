package org.oops.api.alert.controller;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oops.api.alert.dto.CreateAlertDTO;
import org.oops.api.alert.dto.GetAlertResponseDTO;
import org.oops.api.alert.dto.UpdateAlertDTO;
import org.oops.api.alert.service.AlertService;
import org.oops.api.coin.dto.CoinFindByNameDTO;
import org.oops.api.coin.service.CoinService;
import org.oops.api.common.controller.BaseController;
import org.oops.api.common.dto.ResponseDTO;
import org.oops.global.config.auth.dto.SessionUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/alert")
@RestController
public class AlertRestController extends BaseController {

    private final AlertService alertService;
    private final CoinService coinService;

    //알림 생성
    @PostMapping("/save")
    public ResponseEntity<ResponseDTO<CreateAlertDTO.CreateAlertResponseDTO>> saveAlert(
            @RequestBody CreateAlertDTO.CreateAlertRequestDTO requestDTO, HttpSession httpSession) {

        // 세션에서 유저 정보 가져오기
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if (user == null) {
            // 세션에 유저가 없으면 에러 응답
            return ResponseEntity.status(401).body(ResponseDTO.error("유저 정보가 없습니다."));
        }

        // 유저의 이메일 가져오기
        String email = user.getEmail();

        // 코인 이름으로 코인 조회
        CoinFindByNameDTO coin = coinService.findCoinByCoinName(requestDTO.getCoinName());
        if (coin == null) {
            // 코인을 찾을 수 없으면 에러 응답
            return ResponseEntity.status(404).body(ResponseDTO.error("해당 코인을 찾을 수 없습니다."));
        }

        // 알림 저장 시 email도 함께 전달
        CreateAlertDTO.CreateAlertResponseDTO response = alertService.saveAlert(email, requestDTO);

        log.info("컨트롤러 확인:" + response.getAlertActive()+response.getAlertId()+response.getAlertPrice()+response.getUserId()+response.getCoinId());

        // 성공 응답 반환
        return ResponseEntity.ok(ResponseDTO.ok(response));
    }


    //내 알림 리스트 조회
    @GetMapping(value = "/read")
    public ResponseDTO<List<GetAlertResponseDTO>> getAlert(HttpSession session){
        SessionUser principal = (SessionUser) session.getAttribute("user");
        return ResponseDTO.ok(alertService.getAlertByUserId(principal.getId()));
    }

    //내 알림 개수 조회
    @GetMapping(value = "count")
    public Map<String, Integer> countAlert(HttpSession session){
        SessionUser user = (SessionUser) session.getAttribute("user");
        int totalAlerts = alertService.getTotalAlertCount(user.getId());
        int activeAlerts = alertService.getActiveAlertCount(user.getId());
        int inactiveAlerts = totalAlerts - activeAlerts;

        return Map.of(
                "total", totalAlerts,
                "on", activeAlerts,
                "off", inactiveAlerts
        );
    }

    // 알림 활성화 여부 수정 -> 부분 업데이트: PATCH
    @PatchMapping("/update/{alertId}")
    public ResponseEntity<ResponseDTO<Void>> updateAlertStatus(
            @PathVariable Long alertId,
            @RequestBody Map<String, Boolean> requestBody) {

        Boolean alertActive = requestBody.get("alertActive");

        // 알림 상태 업데이트 서비스 호출
        alertService.updateAlertStatus(alertId, alertActive);

        return ResponseEntity.ok(ResponseDTO.ok(null));
    }

    // 알림 가격 수정 -> 부분 업데이트: PATCH
    @PatchMapping("/update/price/{alertId}")
    public ResponseEntity<ResponseDTO<Void>> updateAlertPrice(
            @PathVariable Long alertId,
            @RequestBody Map<String, BigDecimal> requestBody) {

        BigDecimal alertPrice = requestBody.get("alertPrice");

        // 알림 상태 업데이트 서비스 호출
        alertService.updateAlertPrice(alertId, alertPrice);

        return ResponseEntity.ok(ResponseDTO.ok(null));
    }


    //알림 삭제
    @DeleteMapping(value = "/delete/{alertId}")
    public ResponseDTO<Void> deleteAlert(@PathVariable(value = "alertId") Long alertId){
        alertService.deleteAlertById(alertId);
        return ResponseDTO.ok();
    }
}
