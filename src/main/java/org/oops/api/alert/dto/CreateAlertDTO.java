package org.oops.api.alert.dto;

import lombok.Builder;
import lombok.Getter;
import org.oops.domain.alert.Alert;

import java.math.BigDecimal;

public class CreateAlertDTO {

    @Getter
    public static class CreateAlertRequestDTO{
        private BigDecimal alertPrice;
        private Boolean alertActive;
        private String coinName;
        private String alertCondition;


        @Builder
        public CreateAlertRequestDTO(BigDecimal alertPrice, Boolean alertActive, String coinName, String alertCondition) {
            this.alertPrice = alertPrice;
            this.alertActive = alertActive;
            this.coinName = coinName;
            this.alertCondition = alertCondition;
        }
    }

    @Getter
    public static class CreateAlertResponseDTO{
        private Long alertId;
        private BigDecimal alertPrice;
        private Boolean alertActive;
        private Long coinId;
        private Long userId;
        private String coinName;
        private String ticker;
        private String alertCondition;

        @Builder
        public CreateAlertResponseDTO(Long alertId, BigDecimal alertPrice, Boolean alertActive, Long coinId, Long userId, String coinName, String ticker, String alertCondition) {
            this.alertId = alertId;
            this.alertPrice = alertPrice;
            this.alertActive = alertActive;
            this.coinId = coinId;
            this.userId = userId;
            this.coinName = coinName;
            this.ticker = ticker;
            this.alertCondition = alertCondition;
        }

        public static CreateAlertResponseDTO fromEntity(Alert alert) {
            return CreateAlertResponseDTO.builder()
                    .alertId(alert.getAlertId())
                    .alertPrice(alert.getAlertPrice())
                    .alertActive(alert.getAlertActive())
                    .coinId(alert.getCoin().getCoinId())
                    .userId(alert.getUserId().getUserId())
                    .coinName(alert.getCoin().getName())
                    .ticker(alert.getCoin().getTicker())
                    .alertCondition(alert.getAlertCondition())
                    .build();
        }
    }
}
