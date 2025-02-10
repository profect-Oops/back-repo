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
                        user
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


    //알림 수정
    @Override
    @Transactional
    public UpdateAlertDTO.UpdateAlertResponseDTO updateAlert(UpdateAlertDTO.UpdateAlertRequestDTO request){
        Alert alert = alertRepository.findById(request.getAlertId()).orElseThrow(
                () -> new EntityNotFoundException("해당하는 알림이 없습니다.")
        );

        if(request.getAlertActive() != null){
            alert.updateAlertActive(request.getAlertActive());
        }

        if(request.getAlertPrice() != null){
            alert.updateAlertPrice(request.getAlertPrice());
        }

        return UpdateAlertDTO.UpdateAlertResponseDTO.fromEntity(alert);
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



}
