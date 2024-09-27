package fi.re.firebackend.controller.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.re.firebackend.dto.news.NewsDto;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class test {

    public static void main(String[] args) {
        String clientId = "_1H6gLZkRmwKmMiEF5lB"; //애플리케이션 클라이언트 아이디값"
        String clientSecret = "9czeEJ_SdO"; //애플리케이션 클라이언트 시크릿값"

        String text = null;
        try {
            text = URLEncoder.encode("금융", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }


        String apiURL = "https://openapi.naver.com/v1/search/news.json?query=" + text;
        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과


        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            NewsDto newsDto = objectMapper.readValue(responseBody, NewsDto.class);
            // 필요한 데이터 사용
            // 총 기사 수 출력
            System.out.println("총 기사 수: " + newsDto.getTotal());

            // 실제 가져온 기사 수 출력
            System.out.println("가져온 기사 수: " + newsDto.getItems().size());
            System.out.println(newsDto.getTotal());
            for (NewsDto.Item item : newsDto.getItems()) {
                System.out.println(item.getTitle());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}