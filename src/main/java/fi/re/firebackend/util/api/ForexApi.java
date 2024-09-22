package fi.re.firebackend.util.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@PropertySource({"classpath:/application.properties"})
public class ForexApi {

    @Value("${forex.url}")
    private String API_URL;

    @Value("${forex.api_key}")
    private String AUTH_KEY;

    public String getForexData(String searchDate) throws IOException {
        String data = "AP01"; //AP01 : 환율, AP02 : 대출금리, AP03 : 국제금리

        // UriComponentsBuilder로 URL 생성
        String urlString = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("authkey", AUTH_KEY)
                .queryParam("searchdate", searchDate)
                .queryParam("data", data)
                .build()
                .toUriString();
        URL url = new URL(urlString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String result = sb.toString();
        //1 : 성공, 2 : DATA코드 오류, 3 : 인증코드 오류, 4 : 일일제한횟수 마감
        System.out.println(result);

        return result;
    }
}
