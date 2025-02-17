package org.oops.domain.alert;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.oops.domain.coin.Coin;
import org.oops.domain.user.User;

import java.math.BigDecimal;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor
@Table(name = "ALERT")
public class Alert {

    @Id
    @Column(name = "ALERT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    @Column(name = "ALERT_PRICE")
    private BigDecimal alertPrice;

    @Column(name = "ALERT_ACTIVE", nullable = false)
    @ColumnDefault("true")
    private Boolean alertActive;

    @ManyToOne
    @JoinColumn(name = "COIN_ID")
    private Coin coin;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User userId;

    @Column(name = "COIN_NAME")
    private String coinName;

    @Column(name = "COIN_TICKER")
    private String coinTicker;

    @Column(name = "EMAIL")
    private String email;

    @Builder
    public Alert(BigDecimal alertPrice, Boolean alertActive, Coin coin, User userId, String coinName, String coinTicker, String email) {
        this.alertPrice = alertPrice;
        this.alertActive = alertActive;
        this.coin = coin;
        this.userId = userId;
        this.coinName = coinName;
        this.coinTicker = coinTicker;
        this.email = email;
    }

    public static final Alert fromDTO(final BigDecimal alertPrice, final Boolean alertActive, final Coin coin, final User userId, final String coinName, final String coinTicker, final String email) {
        return Alert.builder()
                .alertPrice(alertPrice)
                .alertActive(alertActive)
                .coin(coin)
                .userId(userId)
                .coinName(coinName)
                .coinTicker(coinTicker)
                .email(email)
                .build();
    }

    public void updateAlertActive(Boolean alertActive) {
        this.alertActive = alertActive;
    }

    public void updateAlertPrice(BigDecimal alertPrice) {
        this.alertPrice = alertPrice;
    }
}

