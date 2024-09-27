package fi.re.firebackend.service.news;

import fi.re.firebackend.dto.news.NewsDto;
import fi.re.firebackend.util.api.NewsApi;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class NewsService {

    private final NewsApi newsApi;

    public NewsService(NewsApi newsApi) {
        this.newsApi = newsApi;
    }

    public NewsDto getNews(String query) {
        try {
            return newsApi.fetchNews(query);
        } catch (IOException e) {
            throw new RuntimeException("뉴스 데이터를 가져오는 데 실패했습니다.", e);
        }
    }
}
