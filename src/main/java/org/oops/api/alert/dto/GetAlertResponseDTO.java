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
    private String coinName;
    private String ticker;
    private String email;
    private String alertCondition;

    @Builder
    public GetAlertResponseDTO(Long alertId, BigDecimal alertPrice, Boolean alertActive, CoinDTO coin, Long userId, String coinName, String ticker, String email, String alertCondition) {
        this.alertId = alertId;
        this.alertPrice = alertPrice;
        this.alertActive = alertActive;
        this.coin = coin;
        this.userId = userId;
        this.coinName = coinName;
        this.ticker = ticker;
        this.email = email;
        this.alertCondition = alertCondition;
    }

    public static GetAlertResponseDTO fromEntity(Alert alert) {
        return GetAlertResponseDTO.builder()
                .alertId(alert.getAlertId())
                .alertPrice(alert.getAlertPrice())
                .alertActive(alert.getAlertActive())
                .coin(CoinDTO.fromEntity(alert.getCoin()))
                .userId(alert.getUserId().getUserId())
                .coinName(alert.getCoin().getName())
                .ticker(alert.getCoin().getTicker())
                .email(alert.getEmail())
                .alertCondition(alert.getAlertCondition())
                .build();
    }

}
