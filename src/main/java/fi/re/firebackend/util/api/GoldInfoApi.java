package fi.re.firebackend.util.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
@PropertySource({"classpath:/application.properties"})
public class GoldInfoApi {

    @Value("${gold.url}")
    private String API_URL;

    @Value("${gold.api_key}")
    private String SERVICE_KEY;

    public String getGoldData(String dateType, String endBasDt, int days) throws IOException {

//        String apiUrl = "https://apis.data.go.kr/1160100/service/GetGeneralProductInfoService/getGoldPriceInfo";
//        String serviceKey = ""; //서비스키
//        String numOfRows = "10";	//몇 개 가져올건지
        String pageNo = "1";    //페이지 번호
        String resultType = "json"; // 결과 형식
        String basDt = "20240913"; //기준일자와 일치하는 하루 검색
        String beginBasDt = "20220919"; //기준일자 : 기준 일자보다 크거나 같은 데이터를 검색
//        String endBasDt = "오늘 날짜"; //기준일자가 검색값보다 작은 데이터를 검색
        String likeSrtnCd = "4020000";
        String refDataName = dateType;
        String refDate = endBasDt; //기준이 되는 날짜
        String numOfRows = Integer.toString(days);

//        StringBuilder urlBuilder = new StringBuilder(API_URL);
//        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + SERVICE_KEY);
//        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
//        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8"));
//        urlBuilder.append("&" + URLEncoder.encode("resultType", "UTF-8") + "=" + URLEncoder.encode(resultType, "UTF-8"));
//        urlBuilder.append("&" + URLEncoder.encode(refDataName, "UTF-8") + "=" + URLEncoder.encode(refDate, "UTF-8"));
//        URL url = new URL(urlBuilder.toString());

        // UriComponentsBuilder로 URL 생성
        String urlString = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("ServiceKey", SERVICE_KEY)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("resultType", resultType)
                .queryParam("likeSrtnCd", likeSrtnCd)
                .queryParam(refDataName, refDate)
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
        System.out.println(result);

        return result;
    }
}