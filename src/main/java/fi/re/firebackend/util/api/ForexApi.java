package fi.re.firebackend.util.api;

import fi.re.firebackend.dto.forex.ForexDto;
import fi.re.firebackend.dto.forex.ForexWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@PropertySource({"classpath:/application.properties"})
public class ForexApi {
    private static final Logger log = Logger.getLogger(ForexApi.class);
    private static final JSONParser parser = new JSONParser();
    private final RestTemplate restTemplate;
    @Value("${forex.url}")
    private String API_URL;
    @Value("${forex.api_key}")
    private String AUTH_KEY;

    public ForexWrapper getForexData(String searchDate) throws ParseException {
        String data = "AP01";
        List<ForexDto> forexList = new ArrayList<>();
        int maxAttempts = 1; // 최대 시도 횟수
        int attempts = 0;

        // searchDate를 LocalDate로 변환
        LocalDate date = LocalDate.parse(searchDate, DateTimeFormatter.ofPattern("yyyyMMdd"));

        // API 데이터 가져오기
        while (forexList.isEmpty() && attempts < maxAttempts) {
            String urlString = buildUrl(data, date);

            try {
                forexList = fetchForexData(urlString);
                if (forexList.isEmpty()) {
                    date = date.minusDays(1); // 데이터가 비어있으면 하루 감소
                    log.info("No data at " + date + ", trying one day earlier.");
                }
            } catch (Exception e) {
                log.error("Error during API request: " + e.getMessage());
                break;
            }

            attempts++; // 시도 횟수 증가
        }

        // 최대 시도 횟수에 도달하거나 데이터가 비어있지 않으면 반환
        LocalDate effectiveDate = !forexList.isEmpty() ? date : date.minusDays(1);

        return new ForexWrapper(forexList, effectiveDate); // 데이터와 날짜 반환
    }

    public String buildUrl(String data, LocalDate date) {
        return UriComponentsBuilder.fromHttpUrl(API_URL).queryParam("authkey", AUTH_KEY).queryParam("data", data).queryParam("searchdate", date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).toUriString();
    }

    private List<ForexDto> fetchForexData(String urlString) throws ParseException {
        List<ForexDto> forexList = new ArrayList<>();
        ResponseEntity<String> response = restTemplate.getForEntity(urlString, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String body = response.getBody();
            if (body != null) {
                JSONArray exchangeRateInfoList = (JSONArray) parser.parse(body);
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
        } else {
            log.error("Failed to fetch data, status code: " + response.getStatusCode());
        }

        return forexList;
    }
}
