package org.oops.domain.coin;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor
@Table(name = "COIN")
public class Coin {

    @Id
    @Column(name = "COIN_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coinId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PROSPECTS")
    private BigDecimal prospects;

    @Column(name = "COIN_PICTURE")
    private String picture;

    @Column(name = "ISVisible", nullable = false)
    @ColumnDefault("true")
    private Boolean isVisible;

    @Column(name = "TICKER")
    private String ticker;

    @Builder
    public Coin(String name, BigDecimal prospects, String picture, Boolean isVisible, String ticker) {
        this.name = name;
        this.prospects = prospects;
        this.picture = picture;
        this.isVisible = isVisible;
        this.ticker = ticker;
    }

    @Builder
    public Coin(String name, String picture, String ticker) {
        this.name = name;
        this.picture = picture;
        this.ticker = ticker;
    }

    public static final Coin fromDTO(final String name, final BigDecimal prospects, final String picture, final Boolean isVisible, final String ticker) {
        return Coin.builder()
                .name(name)
                .prospects(prospects)
                .picture(picture)
                .isVisible(isVisible)
                .ticker(ticker)
                .build();
    }

    public void updateIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    @PrePersist
    public void prePersist() {
        if (this.isVisible == null) {
            this.isVisible = false;
        }
    }
}
