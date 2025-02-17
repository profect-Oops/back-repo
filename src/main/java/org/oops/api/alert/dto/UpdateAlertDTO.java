package org.oops.api.alert.dto;

import lombok.Builder;
import lombok.Getter;
import org.oops.domain.alert.Alert;

import java.math.BigDecimal;

public class UpdateAlertDTO {

    @Getter
    public static class UpdateAlertRequestDTO{
        private Long alertId;
        private BigDecimal alertPrice;
        private Boolean alertActive;
        private Long coinId;
        private Long userId;
        private String coinName;

        public UpdateAlertRequestDTO(Long alertId, Boolean alertActive, BigDecimal alertPrice, Long coinId, Long userId, String coinName) {
            this.alertId = alertId;
            this.alertPrice = alertPrice;
            this.alertActive = alertActive;
            this.coinId = coinId;
            this.userId = userId;
            this.coinName = coinName;
        }
    }

    @Getter
    public static class UpdateAlertResponseDTO{
        private final BigDecimal alertPrice;
        private final Boolean alertActive;

        @Builder
        public UpdateAlertResponseDTO(BigDecimal alertPrice, Boolean alertActive) {
            this.alertPrice = alertPrice;
            this.alertActive = alertActive;
        }

        public static UpdateAlertResponseDTO fromEntity(Alert alert){
            return UpdateAlertResponseDTO.builder()
                    .alertActive(alert.getAlertActive())
                    .alertPrice(alert.getAlertPrice())
                    .build();
        }
    }
}
