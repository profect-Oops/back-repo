package org.oops.domain.newscoinrelation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.oops.domain.coin.Coin;
import org.oops.domain.news.News;

@Entity
@Getter
@NoArgsConstructor
@DynamicUpdate
@Table(name = "NEWS_COIN_RELATION")
public class NewsCoinRelation {

    @Id
    @ManyToOne
    @JoinColumn(name = "NEWS_ID", nullable = false)
    private News news;

    @Id
    @ManyToOne
    @JoinColumn(name = "COIN_ID", nullable = false)
    private Coin coin;

}
