package fi.re.firebackend.dto.news;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsDto {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<Item> items;

    // 에러 필드 추가
    private String errorMessage;
    private String errorCode;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private String title;
        @JsonProperty("originallink")
        private String originallink;
        private String link;
        private String description;
        @JsonProperty("pubDate")
        private String pubDate;


    }
}
