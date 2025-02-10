package org.oops.domain.alert;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    @EntityGraph(attributePaths = {"coin"})  // coin 객체를 함께 로딩
    List<Alert> findByUserId_UserId(Long userId);  //내 알림 조회
}
