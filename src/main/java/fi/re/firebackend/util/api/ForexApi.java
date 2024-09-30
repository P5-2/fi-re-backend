package fi.re.firebackend.util.api;

import fi.re.firebackend.dto.forex.ForexDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource({"classpath:/application.properties"})
public class ForexApi {
    JSONParser parser = new JSONParser();
    @Value("${forex.url}")
    private String API_URL;

    @Value("${forex.api_key}")
    private String AUTH_KEY;

    public List<ForexDto> getForexData(String searchDate) throws IOException, ParseException {
        String data = "AP01";
        int retryCount = 5; // 재시도 횟수
        int attempt = 0; // 현재 시도 횟수
        List<ForexDto> forexList = new ArrayList<>();

        while (attempt < retryCount) {
            attempt++;
            // UriComponentsBuilder로 URL 생성
            String urlString = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("authkey", AUTH_KEY)
                    .queryParam("data", data)
                    .queryParam("searchdate", searchDate)
                    .build()
                    .toUriString();

            System.out.println("UrlString: " + urlString);

            HttpURLConnection conn = null;
            BufferedReader rd = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection.setFollowRedirects(false);
                System.setProperty("https.protocols", "TLSv1.1,TLSv1.2");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                System.out.println("Response code: " + conn.getResponseCode());

                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    InputStream inputStream = conn.getInputStream();
                    if (inputStream != null) {
                        rd = new BufferedReader(new InputStreamReader(inputStream));
                    }

                    String line;

                    while ((line = rd.readLine()) != null) {
                        JSONArray exchangeRateInfoList = (JSONArray) parser.parse(line);

                        for (Object o : exchangeRateInfoList) {
                            JSONObject exchangeRateInfo = (JSONObject) o;
                            ForexDto forexDto = new ForexDto();

                            forexDto.setCurUnit(exchangeRateInfo.getOrDefault("cur_unit", "").toString());
                            forexDto.setDealBasR(exchangeRateInfo.getOrDefault("deal_bas_r", "").toString());
                            forexDto.setCurNm(exchangeRateInfo.getOrDefault("cur_nm", "").toString());
                            forexDto.setBkpr(exchangeRateInfo.getOrDefault("bkpr", "").toString());
                            forexDto.setTtb(exchangeRateInfo.getOrDefault("ttb", "").toString());
                            forexDto.setTts(exchangeRateInfo.getOrDefault("tts", "").toString());
                            forexDto.setYyEfeeR(exchangeRateInfo.getOrDefault("yy_efee_r", "").toString());
                            forexDto.setTenDdEfeeR(exchangeRateInfo.getOrDefault("ten_dd_efee_r", "").toString());
                            forexDto.setKftcBkpr(exchangeRateInfo.getOrDefault("kftc_bkpr", "").toString());
                            forexDto.setKftcDealBasR(exchangeRateInfo.getOrDefault("kftc_deal_bas_r", "").toString());

                            forexList.add(forexDto);
                        }
                    }
                    break; // 성공 시 while 루프 종료
                } else {
                    throw new IOException("Invalid response code: " + conn.getResponseCode());
                }
            } catch (IOException | ParseException e) {
                System.err.println("API 요청 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
                if (attempt == retryCount) {
                    throw e; // 재시도 횟수 초과 시 예외를 다시 던짐
                }
            } finally {
                try {
                    if (rd != null) rd.close();
                    if (conn != null) conn.disconnect();
                } catch (IOException ex) {
                    System.err.println("자원 해제 중 오류 발생: " + ex.getMessage());
                }
            }
        }

        return forexList;
    }

}
