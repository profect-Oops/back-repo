package org.oops.api.alert.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oops.api.alert.dto.CreateAlertDTO;
import org.oops.api.alert.dto.GetAlertResponseDTO;
import org.oops.api.alert.dto.UpdateAlertDTO;
import org.oops.domain.alert.Alert;
import org.oops.domain.alert.AlertRepository;
import org.oops.domain.coin.Coin;
import org.oops.domain.coin.CoinRepository;
import org.oops.domain.user.User;
import org.oops.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final CoinRepository coinRepository;

    //알림 저장
    @Override
    public CreateAlertDTO.CreateAlertResponseDTO saveAlert(String email, CreateAlertDTO.CreateAlertRequestDTO request){
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("해당 유저가 없습니다.")
        );

        Coin coin = coinRepository.findByName(request.getCoinName()).orElseThrow(
                () -> new EntityNotFoundException("해당 코인이 없습니다.")
        );

        log.info("서비스 확인 Inserting alert: alertActive = {}, alertPrice = {}, coinId = {}, userId = {}",
                request.getAlertActive(), request.getAlertPrice(), coin.getCoinId(), user.getUserId());


        Alert alert = alertRepository.save(
                Alert.fromDTO(
                        request.getAlertPrice(),
                        request.getAlertActive(),
                        coin,
                        user,
                        request.getCoinName(),
                        coin.getTicker(),
                        email
                )
        );
        return CreateAlertDTO.CreateAlertResponseDTO.fromEntity(alert);
    }

    //내 알림 리스트 보기
    @Override
    @Transactional(readOnly = true)
    public List<GetAlertResponseDTO> getAlertByUserId(Long userId){
        return alertRepository.findByUserId_UserId(userId).stream().map(GetAlertResponseDTO::fromEntity).toList();
    }

    //알림 하나 상세보기
    @Override
    @Transactional(readOnly = true)
    public GetAlertResponseDTO getAlertByAlertId(Long alertId){
        Alert alert = alertRepository.findById(alertId).orElseThrow(
                () -> new EntityNotFoundException("해당되는 알림이 없습니다.")
        );
        return GetAlertResponseDTO.fromEntity(alert);
    }

    //알림 활성화 여부 수정
    @Override
    @Transactional
    public void updateAlertStatus(Long alertId, Boolean alertActive){
        // 알림 ID로 해당 알림 조회
        Alert alert = alertRepository.findById(alertId).orElse(null);

        if (alert == null) {
            throw new IllegalArgumentException("알림을 찾을 수 없습니다.");
        }

        // alertActive 값 변경
        alert.updateAlertActive(alertActive);

        // 알림 상태 업데이트
        alertRepository.save(alert);
    }

    //알림 가격 수정
    @Override
    @Transactional
    public void updateAlertPrice(Long alertId, BigDecimal alertPrice){
        // 알림 ID로 해당 알림 조회
        Alert alert = alertRepository.findById(alertId).orElse(null);

        if (alert == null) {
            throw new IllegalArgumentException("알림을 찾을 수 없습니다.");
        }

        // alertPrice 값 수정
        alert.updateAlertPrice(alertPrice);

        // 알림 상태 업데이트
        alertRepository.save(alert);
    }


    //알림 삭제
    @Override
    @Transactional
    public void deleteAlertById(Long alertId){
        try{
            alertRepository.deleteById(alertId);
        }catch (IllegalArgumentException ex){
            throw new EntityNotFoundException("해당 알림이 없습니다.");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //내 알림의 총 개수 조회
    @Override
    @Transactional(readOnly = true)
    public int getTotalAlertCount(Long userId){
        return alertRepository.countByUserId_UserId(userId);
    }

    //내 알림 중 알림 활성화 된 알림 개수 조회
    @Override
    @Transactional(readOnly = true)
    public int getActiveAlertCount(Long userId){
        return alertRepository.countByUserId_UserIdAndAlertActiveTrue(userId);
    }

}
