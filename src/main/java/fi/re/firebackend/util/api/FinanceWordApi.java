package fi.re.firebackend.util.api;

import fi.re.firebackend.dto.financeWord.FinanceWordDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource({"classpath:/application.properties"})
public class FinanceWordApi {
    @Value("${financeWord.url}")
    private String API_URL;

    @Value("${financeWord.api_key}")
    private String SERVICE_KEY;

    public List<FinanceWordDto> getFinanceWordData() throws IOException, ParserConfigurationException, SAXException {
        String pageNo = "1";    //페이지 번호
        String numOfRows = "500";

        // UriComponentsBuilder로 URL 생성
        String urlString = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .build()
                .toUriString();

        URL url = new URL(urlString);
        System.out.println(url);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-type", "application/json");
//        System.out.println("Response code: " + conn.getResponseCode());
//        BufferedReader rd;
//        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        } else {
//            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//        }
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = rd.readLine()) != null) {
//            sb.append(line);
//        }
//        rd.close();
//        conn.disconnect();
//        String result = sb.toString();
//        System.out.println(result);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(urlString);

        doc.getDocumentElement().normalize();
        System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
        NodeList nodeList = doc.getElementsByTagName("item");

        List<FinanceWordDto> list = new ArrayList<>(); //금융용어 데이터를 api로 받아서 list 넘기기
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Element element = (Element) node;
//            System.out.println("금융용어: "+element.getElementsByTagName("fnceDictNm").item(0).getTextContent());
//            System.out.println("용어설명: "+element.getElementsByTagName("ksdFnceDictDescContent").item(0).getTextContent());
            list.add(
                    new FinanceWordDto(
                            0,
                            element.getElementsByTagName("fnceDictNm").item(0).getTextContent(),
                            element.getElementsByTagName("ksdFnceDictDescContent").item(0).getTextContent()
                    )
            );
        }
        return list;
    }
}
