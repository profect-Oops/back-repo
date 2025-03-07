package org.oops.domain.alert;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    //내 알림 조회
    @EntityGraph(attributePaths = {"coin"})  // coin 객체를 함께 로딩
    List<Alert> findByUserId_UserId(Long userId);

    //내 알림 개수 조회
    int countByUserId_UserId(Long userId);

    //내 알림 중 활성화된 알림 개수 조회
    int countByUserId_UserIdAndAlertActiveTrue(Long userId);

    // 코인 티커에 해당하고 alertActive가 true인 알람 목록 조회
    List<Alert> findByCoinTickerAndAlertActiveTrue(String coinTicker);
}
