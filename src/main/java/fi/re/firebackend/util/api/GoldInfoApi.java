package fi.re.firebackend.util.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@PropertySource({"classpath:/application.properties"})
public class GoldInfoApi {

    @Value("${gold.url}")
    private String API_URL;

    @Value("${gold.api_key}")
    private String SERVICE_KEY;

    public String getGoldData(String dateType, String endBasDt, int days) throws IOException {
        String pageNo = "1";    //페이지 번호
        String resultType = "json"; // 결과 형식
        String likeSrtnCd = "4020000";
        String refDataName = dateType;
        String refDate = endBasDt; //기준이 되는 날짜
        String numOfRows = Integer.toString(days);

        // UriComponentsBuilder로 URL 생성
        String urlString = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("resultType", resultType)
                .queryParam("likeSrtnCd", likeSrtnCd)
                .queryParam(refDataName, refDate)
                .build()
                .toUriString();
        URL url = new URL(urlString);
        System.out.println(url);
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
//        System.out.println(result);

        return result;
    }
}