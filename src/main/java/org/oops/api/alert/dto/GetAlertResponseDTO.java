package org.oops.api.alert.dto;

import lombok.Builder;
import lombok.Getter;
import org.oops.api.coin.dto.CoinDTO;
import org.oops.domain.alert.Alert;

import java.math.BigDecimal;

@Getter
public class GetAlertResponseDTO {

    private Long alertId;
    private BigDecimal alertPrice;
    private Boolean alertActive;
    private CoinDTO coin;
    private Long userId;

    @Builder
    public GetAlertResponseDTO(Long alertId, BigDecimal alertPrice, Boolean alertActive, CoinDTO coin, Long userId) {
        this.alertId = alertId;
        this.alertPrice = alertPrice;
        this.alertActive = alertActive;
        this.coin = coin;
        this.userId = userId;
    }

    public static GetAlertResponseDTO fromEntity(Alert alert) {
        return GetAlertResponseDTO.builder()
                .alertId(alert.getAlertId())
                .alertPrice(alert.getAlertPrice())
                .alertActive(alert.getAlertActive())
                .coin(CoinDTO.fromEntity(alert.getCoin()))
                .userId(alert.getUserId().getUserId())
                .build();
    }

}
