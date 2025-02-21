package org.oops.domain.news;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.oops.domain.coin.Coin;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@DynamicUpdate
@Table(name = "NEWS")
public class News {

    @Id
    @Column(name = "NEWS_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "TITLE_EN")
    private String titleEN;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "CONTENT_EN")
    private String contentEN;

    @Column(name = "NEWSPAPER")
    private String newspaper;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "UPLOADTIME")
    private LocalDateTime uploadTime;

    @ManyToOne
    @JoinColumn(name = "COIN_ID")
    private Coin coinId;

    @Builder
    public News(String title, String content, String newspaper, String source, LocalDateTime uploadTime,Coin coinId) {
        this.title = title;
        this.content = content;
        this.newspaper = newspaper;
        this.source = source;
        this.uploadTime = uploadTime;
        this.coinId = coinId;
    }

    public static final News fromDTO(final String title, final String content, final String newspaper, final String source, final LocalDateTime uploadTime, final Coin coinId) {
        return News.builder()
                .title(title)
                .content(content)
                .newspaper(newspaper)
                .source(source)
                .uploadTime(uploadTime)
                .coinId(coinId)
                .build();
    }
}
