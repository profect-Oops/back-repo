package org.oops.domain.alert;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.oops.domain.coin.Coin;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @Column(name = "ALERT_ACTIVE")
    private Boolean alertActive;

    @ManyToOne
    @JoinColumn(name = "COIN_ID")
    private Coin coinId;

}

