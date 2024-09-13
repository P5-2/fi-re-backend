package fi.re.firebackend.service.gold;

import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.dto.gold.LSTMData;
import fi.re.firebackend.util.api.JsonConverter;
import fi.re.firebackend.util.dateUtil.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

@Service
public class GoldServiceImpl implements GoldService{

    private final RestTemplate restTemplate;

    @Autowired
    public GoldServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //DB에 api에서 받은 정보 저장하는 함수
//    public String setGoldData(){
//        //
//    }



    @Override
    public int checkLastUpdateDate() {

        return 0;
    }

    @Override
    public GoldInfo getDaysInfo(String basDt) {
        return null;
    }

    @Override
    public List<GoldInfo> getAllGoldInfoInPeriod(String endBasDt) {
//        return List.of();
        return null;
    }

    //현재 날짜로부터 1년 전까지의 데이터를 받아오는 함수
    //매개변수로 받는 endBasDt와 OpenApi의 endBasDt는 다름
    @Override
    public List<GoldInfo> getAllGoldInfoInPeriod(String endBasDt, int months) {
        DateUtil dateUtil;


//        return List.of();
        return null;
    }


    // 예측된 금 값 받아오는 함수
    @Override
    public List<GoldInfo> getFutureGoldPrice() throws Exception {
        try{
            LSTMData data;

            String date = "20240913";
            return JsonConverter.convertJsonToList(getGoldData("basDt", date));
        }catch (Exception e){
            throw new Exception(e.toString());
        }
    }

    // getGoldData의 dateType은 basDt, beginBasDt, endBasDt 중 하나
    // basDt : 기준일자로 해당되는 하루의 금 값 정보 요청
    // beginBasDt : 입력한 일자 이후 현재까지의 모든 일자 정보
    // endBasDt : 입력한 일자 이전의 모든 정보
    //Open API를 이용하여 금 시세 가져오는 함수(데이터가 없을 때나 하루의 시작에 한 번만 가져옴)
    public String getGoldData(String dateType, String queryDate) {
        String apiUrl = "https://apis.data.go.kr/1160100/service/GetGeneralProductInfoService/getGoldPriceInfo";
        String serviceKey = "NfkNz94MvmtFAx%2Fs5jLbtZw%2FgYQgMbzMgvaHKWD0c%2BaSa8JqBy2jTLvmQgAv27%2F6%2B7mHYYn2L1FJspaFCMpLzw%3D%3D"; //인증 키
        String numOfRows = "10";
        String pageNo = "1";
        String resultType = "json";
        //어떤 날짜로 받아와야할지 수정하기
        String basDt = "20240913"; //
        String beginBasDt = "20220919"; //마지막으로 최신화한 날짜 저장하고 유동적으로 변경하기
        String endBasDt = "오늘 날짜"; //db에 저장할거 한 번만 저장하면 될 듯

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        try {
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("resultType", "UTF-8") + "=" + URLEncoder.encode(resultType, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode(dateType, "UTF-8") + "=" + URLEncoder.encode(queryDate, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("resultType", "UTF-8") + "=" + URLEncoder.encode(resultType, "UTF-8"));
            // API 호출
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

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
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while fetching data. "+e;
        }
    }
}
