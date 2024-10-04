package fi.re.firebackend.util.api;

import fi.re.firebackend.dto.forex.ForexDto;
import fi.re.firebackend.dto.forex.ForexWrapper;
import org.apache.log4j.Logger;
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
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource({"classpath:/application.properties"})
public class ForexApi {
    private static final Logger log = Logger.getLogger(ForexApi.class);
    private static final JSONParser parser = new JSONParser();
    private static final int MAX_REDIRECTS = 5;

    private String API_URL= "https://www.koreaexim.go.kr/site/program/financial/exchangeJSON";

    @Value("${forex.api_key}")
    private String AUTH_KEY;

    public ForexWrapper getForexData(String searchDate) throws IOException, ParseException {
        String data = "AP01";
        int retryCount = 5; // 재시도 횟수
        List<ForexDto> forexList = new ArrayList<>();

        // searchDate를 LocalDate로 변환
        LocalDate date = LocalDate.parse(searchDate, DateTimeFormatter.ofPattern("yyyyMMdd"));

        while (forexList.isEmpty() && retryCount > 0) {
            String urlString = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("authkey", AUTH_KEY)
                    .queryParam("data", data)
                    .queryParam("searchdate", date)
                    .build()
                    .toUriString();

            try {
                forexList = fetchForexData(urlString, date);
                if (forexList.isEmpty()) {
                    // 데이터가 비어있으면 searchDate를 하루 감소
                    date = date.minusDays(1);
                    log.info("no data at " + date + " minus one day earlier");
                }
            } catch (UnknownHostException e) {
                log.error("network error: " + e.getMessage());
                return new ForexWrapper(forexList, date);
            } catch (IOException | ParseException e) {
                log.error("API 요청 중 오류 발생: " + e.getMessage());
                retryCount--; // 재시도 횟수 감소
                if (retryCount == 0) {
                    throw e; // 재시도 횟수 초과 시 예외를 다시 던짐
                }
            }
        }

        return new ForexWrapper(forexList, date); // 검색된 데이터와 요청한 날짜를 포함하여 반환
    }


    private List<ForexDto> fetchForexData(String urlString, LocalDate date) throws IOException, ParseException {
        List<ForexDto> forexList = new ArrayList<>();
        int redirectCount = 0; // 리다이렉션 횟수

        URL url = new URL(urlString);
        System.setProperty("https.protocols", "TLSv1.1,TLSv1.2");

        while (redirectCount < MAX_REDIRECTS) {
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false); // 수동 리다이렉션 처리
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();
            log.info("Response code: " + responseCode);

            if (responseCode >= 200 && responseCode <= 300) {
                try (InputStream inputStream = conn.getInputStream();
                     BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream))) {

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
                } catch (SocketException e) {
                    if (conn != null) conn.disconnect();
                }
                return forexList; // 성공적으로 데이터를 받았으면 종료
            } else if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                // 리다이렉션 처리
//                String newUrl = conn.getHeaderField("Location");
//                if (!newUrl.startsWith("https://www.koreaexim.go.kr")) {
//                    newUrl = "https://www.koreaexim.go.kr" + newUrl;
//                }
//                log.info("리다이렉션 발생. 새로운 URL: " + newUrl);
//                url = new URL(newUrl);
                redirectCount++;
            } else {
                throw new IOException("Invalid response code: " + responseCode);
            }
        }

        throw new IOException("redirect more than 20 times");
    }
}
