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

    @Column(name = "CONTENT", columnDefinition = "varchar(1000)")
    private String content;

    @Column(name = "NEWSPAPER")
    private String newspaper;

    @Column(name = "WRITER")
    private String writer;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "UPLOADTIME")
    private LocalDateTime uploadTime;

    @Column(name = "NEWS_IMAGE")
    private String newsImage;

    @ManyToOne
    @JoinColumn(name = "COIN_ID")
    private Coin coinId;

    @Builder
    public News(String title, String content, String newspaper, String writer, String source, LocalDateTime uploadTime, String newsImage, Coin coinId) {
        this.title = title;
        this.content = content;
        this.newspaper = newspaper;
        this.writer = writer;
        this.source = source;
        this.uploadTime = uploadTime;
        this.newsImage = newsImage;
        this.coinId = coinId;
    }

    public static final News fromDTO(final String title, final String content, final String newspaper, final String writer, final String source, final LocalDateTime uploadTime, final String newsImage, final Coin coinId) {
        return News.builder()
                .title(title)
                .content(content)
                .newspaper(newspaper)
                .writer(writer)
                .source(source)
                .uploadTime(uploadTime)
                .newsImage(newsImage)
                .coinId(coinId)
                .build();
    }
}
