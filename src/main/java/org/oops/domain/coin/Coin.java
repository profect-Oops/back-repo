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
    @ColumnDefault("0.0")
    private BigDecimal prospects;

    @Column(name = "COIN_PICTURE")
    private String picture;

    @Column(name = "TICKER")
    private String ticker;

    @Column(name = "GPT_DATA", nullable = false)
    private String gptData;

    @Builder
    public Coin(String name, BigDecimal prospects, String picture, String ticker, String gptData) {
        this.name = name;
        this.prospects = prospects;
        this.picture = picture;
        this.ticker = ticker;
        this.gptData = gptData;
    }

    @Builder
    public Coin(String name, String picture, String ticker) {
        this.name = name;
        this.picture = picture;
        this.ticker = ticker;
    }

    public static final Coin fromDTO(final String name, final BigDecimal prospects, final String picture, final String ticker, final  String gptData) {
        return Coin.builder()
                .name(name)
                .prospects(prospects)
                .picture(picture)
                .ticker(ticker)
                .gptData(gptData)
                .build();
    }


    @PrePersist
    public void prePersist() {
        if(this.prospects == null) {
            this.prospects = new BigDecimal(0);
        }
        if(this.gptData == null) {
            this.gptData = "";
        }
    }
}
