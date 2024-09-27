package fi.re.firebackend.util.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.re.firebackend.dto.news.NewsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@PropertySource({"classpath:/application.properties"})
public class NewsApi {

    @Value("${news.client_id}")
    private String CLIENT_ID;

    @Value("${news.client_secret}")
    private String CLIENT_SECRET; // 애플리케이션 클라이언트 시크릿값

    public NewsDto fetchNews(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String apiURL = "https://openapi.naver.com/v1/search/news.json?query=" + encodedQuery;

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", CLIENT_ID);
        requestHeaders.put("X-Naver-Client-Secret", CLIENT_SECRET);

        String responseBody = get(apiURL, requestHeaders);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseBody, NewsDto.class);
    }

    private String get(String apiUrl, Map<String, String> requestHeaders) throws IOException {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readBody(con.getInputStream());
            } else {
                return readBody(con.getErrorStream());
            }
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection connect(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        return (HttpURLConnection) url.openConnection();
    }

    private String readBody(InputStream body) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(body);
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
        }
        return responseBody.toString();
    }
}
