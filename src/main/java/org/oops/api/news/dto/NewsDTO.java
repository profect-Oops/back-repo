package org.oops.api.news.dto;

import lombok.Builder;
import lombok.Getter;
import org.oops.domain.news.News;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class NewsDTO implements Serializable {

    private final Long newsId;

    private final String title;

    private final String content;

    private final String newspaper;

    private final String source;

    private final LocalDateTime uploadTime;


    @Builder
    public NewsDTO(Long newsId, String title, String content, String newspaper, String source, LocalDateTime uploadTime) {
        this.newsId = newsId;
        this.title = title;
        this.content = content;
        this.newspaper = newspaper;
        this.source = source;
        this.uploadTime = uploadTime;
    }

    public static NewsDTO fromEntity(News news){
        return NewsDTO.builder()
                .newsId(news.getNewsId())
                .title(news.getTitle())
                .content(news.getContent())
                .newspaper(news.getNewspaper())
                .source(news.getSource())
                .uploadTime(news.getUploadTime())
                .build();
    }
}
