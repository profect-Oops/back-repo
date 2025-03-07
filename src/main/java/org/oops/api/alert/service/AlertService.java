package org.oops.api.alert.service;

import org.oops.api.alert.dto.CreateAlertDTO;
import org.oops.api.alert.dto.GetAlertResponseDTO;
import org.oops.api.alert.dto.UpdateAlertDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * AlertService 인터페이스는 알림과 관련된 다양한 기능을 제공합니다.
 *
 * @author: MJLee39
 */
public interface AlertService {

    /**
     * 주어진 요청 DTO를 기반으로 새로운 알림을 생성합니다.
     *
     * @param알림 생성을 위해 필요한 정보가 포함된 데이터 전송 객체
     *
     * @return 생성된 알림의 세부 정보를 포함하는 응답 DTO
     */
    CreateAlertDTO.CreateAlertResponseDTO saveAlert(String email, CreateAlertDTO.CreateAlertRequestDTO request);

    /**
     * 주어진 UserID로 알림을 조회합니다.
     *
     * @param조회할 유저 ID
     *
     * @return alert 리스트 응답
     */
    List<GetAlertResponseDTO> getAlertByUserId(Long userId);

    /**
     * 주어진 AlertID로 알림을 조회합니다.
     *
     * @param조회할 alertID
     *
     * @return 조회된 알림의 세부 정보를 포함하는 응답 DTO
     */
    GetAlertResponseDTO getAlertByAlertId(Long alertId);

    /**
     * 알림 활성화 여부 업데이트
     *
     * @param알림 ID, 알림 활성화 여부
     *
     */
    void updateAlertStatus(Long alertId, Boolean alertActive);

    /**
     * 알림 조건 업데이트
     *
     * @param알림 ID, 알림 조건
     *
     */
    void updateAlertCondition(Long alertId, String alertCondition);


    /**
     * 알림 수정 - alertPrice 수정
     *
     * @param알림 ID, price
     */
    void updateAlertPrice(Long alertId, BigDecimal alertPrice);

    /**
     * 주어진 ID로 알림을 삭제합니다.
     *
     * @param삭제할 리뷰의 ID
     */
    void deleteAlertById(Long alertId);

    /**
     * 내 알림의 총 개수 조회
     *
     * @param유저 아이디
     */
    int getTotalAlertCount(Long userId);

    /**
     * 내 알림 중 알림 활성화 된 알림 개수 조회
     */
    int getActiveAlertCount(Long userId);
}
