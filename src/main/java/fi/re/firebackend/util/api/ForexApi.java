package fi.re.firebackend.util.api;


import fi.re.firebackend.dto.forex.ForexDto;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource({"classpath:/application.properties"})
public class ForexApi {

    @Value("${forex.url}")
    private String API_URL;

    @Value("${forex.api_key}")
    private String AUTH_KEY;
    private static HttpURLConnection connection;

    public List<ForexDto> getForexData(String searchDate) throws IOException {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        JSONParser parser = new JSONParser();

        String authKey = AUTH_KEY;
        String dataType = "AP01";
        BigDecimal exchangeRate = null;
        List<ForexDto> forexList = new ArrayList<>();
        try {
            // Request URL
            URL url = new URL(API_URL + authKey + "&searchdate=" + searchDate + "&data=" + dataType);
            connection = (HttpURLConnection) url.openConnection();

            // Request 초기 세팅
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            // API 호출
            // 실패했을 경우 Connection Close
            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else { // 성공했을 경우 환율 정보 추출
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    JSONArray exchangeRateInfoList = (JSONArray) parser.parse(line);

                    // KRW -> USD에 대한 환율 정보 조회
                    for (Object o : exchangeRateInfoList) {
                        JSONObject exchangeRateInfo = (JSONObject) o;
                        // ForexDto 생성 및 리스트에 추가
                        ForexDto forexDto = new ForexDto();
                        forexDto.setCurUnit(exchangeRateInfo.get("cur_unit").toString());
                        forexDto.setDealBasR(exchangeRateInfo.get("deal_bas_r").toString());  // String으로 설정된 deal_bas_r
                        forexDto.setCurNm(exchangeRateInfo.get("cur_nm").toString());
                        forexDto.setBkpr(exchangeRateInfo.get("bkpr").toString());
                        forexDto.setTtb(exchangeRateInfo.get("ttb").toString());
                        forexDto.setTts(exchangeRateInfo.get("tts").toString());
                        forexDto.setYyEfeeR(exchangeRateInfo.get("yy_efee_r").toString());
                        forexDto.setTenDdEfeeR(exchangeRateInfo.get("ten_dd_efee_r").toString());
                        forexDto.setKftcBkpr(exchangeRateInfo.get("kftc_bkpr").toString());
                        forexDto.setKftcDealBasR(exchangeRateInfo.get("kftc_deal_bas_r").toString());

                        forexList.add(forexDto);

                    }
                }


                reader.close();
            }
            System.out.println(responseContent);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }

        return forexList;
    }
}