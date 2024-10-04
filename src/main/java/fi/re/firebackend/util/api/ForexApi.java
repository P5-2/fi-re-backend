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
    private static final int MAX_REDIRECTS = 3;
    private static final int MAX_RETRIES = 1; // 재시도 횟수
    private static final int TIMEOUT = 10000; // 타임아웃

    @Value("${forex.url}")
    private String API_URL;

    @Value("${forex.api_key}")
    private String AUTH_KEY;


    public ForexWrapper getForexData(String searchDate) throws IOException, ParseException {
        String data = "AP01";
        int retryCount = MAX_RETRIES; // 최대 재시도 횟수
        List<ForexDto> forexList = new ArrayList<>();

        // searchDate를 LocalDate로 변환
        LocalDate date = LocalDate.parse(searchDate, DateTimeFormatter.ofPattern("yyyyMMdd"));

        while (forexList.isEmpty() && retryCount > 0) {
            String urlString = buildUrl(data, date);

            try {
                forexList = fetchForexData(urlString);
                if (forexList.isEmpty()) {
                    // 데이터가 비어있으면 searchDate를 하루 감소
                    date = date.minusDays(1);
                    log.info("No data at " + date + ", trying one day earlier.");
                }
            } catch (UnknownHostException e) {
                log.error("Network error: " + e.getMessage());
                return new ForexWrapper(forexList, date);
            } catch (IOException | ParseException e) {
                retryCount--; // 재시도 횟수 감소
                log.error("Error during API request: " + e.getMessage() + ". Retries left: " + (retryCount));
                if (retryCount == 0) {
                    throw e; // 재시도 횟수 초과 시 예외를 다시 던짐
                }
            }
        }

        return new ForexWrapper(forexList, date); // 검색된 데이터와 요청한 날짜를 포함하여 반환
    }

    private String buildUrl(String data, LocalDate date) {
        return UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("authkey", AUTH_KEY)
                .queryParam("data", data)
                .queryParam("searchdate", date.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .build()
                .toUriString();
    }

    private List<ForexDto> fetchForexData(String urlString) throws IOException, ParseException {
        List<ForexDto> forexList = new ArrayList<>();
        int redirectCount = 0; // 리다이렉션 횟수
        URL url = new URL(urlString);
        System.setProperty("https.protocols", "TLSv1.1,TLSv1.2");

        while (redirectCount < MAX_REDIRECTS) {
            HttpURLConnection conn = null;
            try {
                conn = createConnection(url);

                int responseCode = conn.getResponseCode();
                log.info("Response code: " + responseCode);

                if (isSuccessfulResponse(responseCode)) {
                    forexList = parseResponse(conn);
                    return forexList; // 성공적으로 데이터를 받았으면 종료
                } else if (isRedirect(responseCode)) {
                    url = handleRedirect(url, conn);
                    redirectCount++;
                } else {
                    throw new IOException("Invalid response code: " + responseCode);
                }
            } catch (IOException e) {
                log.info("Error during API request: " + e.getMessage());
            }
            finally {
                if (conn != null) {
                    conn.disconnect(); // 리소스 정리
                }
            }
        }

        throw new IOException("Too many redirects (more than " + MAX_REDIRECTS + ").");
    }

    private HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(true);  // 리다이렉션 허용
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);
        return conn;
    }

    private boolean isSuccessfulResponse(int responseCode) {
        return responseCode >= 200 && responseCode < 300;
    }

    private boolean isRedirect(int responseCode) {
        return responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                responseCode == HttpURLConnection.HTTP_SEE_OTHER;
    }

    private URL handleRedirect(URL url, HttpURLConnection conn) throws IOException {
        String newUrl = conn.getHeaderField("Location");
        if (newUrl != null) {
            if (!newUrl.startsWith("http")) {
                newUrl = url.getProtocol() + "://" + url.getHost() + newUrl;
            }

            if (url.getProtocol().equals("https") && newUrl.startsWith("http://")) {
                log.warn("WARNING: Redirecting from HTTPS to HTTP. Proceeding with caution.");
            }

            log.info("Redirecting to: " + newUrl);
            return new URL(newUrl);
        } else {
            throw new IOException("No Location header for redirect.");
        }
    }

    private List<ForexDto> parseResponse(HttpURLConnection conn) throws IOException, ParseException {
        List<ForexDto> forexList = new ArrayList<>();
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
        }
        return forexList;
    }
}
