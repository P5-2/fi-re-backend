package fi.re.firebackend.util.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;


@Component
@PropertySource({"classpath:/application.properties"})
public class SavingsDepositApi {

    private static final Logger log = LoggerFactory.getLogger(SavingsDepositApi.class);
    @Value("${savings.url}")
    private String SAVINGS_API_URL;

    @Value("${deposit.url}")
    private String DEPOSIT_API_URL;

    @Value("${savingsDeposit.api_key}")
    private String AUTH_KEY;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;


//    Json처리와 HTTP요청
    @Autowired
    private SavingsDepositApi(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }


    //적금 상품 목록 조회
    public List<SavingsDepositDto> getAllSavings(String topFinGrpNo, int page, int size) throws IOException {
        String url = buildUrl(SAVINGS_API_URL, topFinGrpNo, page, size);

        return getProductList(url);
    }

    //예금 상품 목록 조회
    public List<SavingsDepositDto> getAllDeposit(String topFinGrpNo, int page, int size) throws IOException {
        String url = buildUrl(DEPOSIT_API_URL, topFinGrpNo, page, size);
//        String url = "https://finlife.fss.or.kr/finlifeapi/depositProductsSearch.json?auth=775c4db3fbe78868197e38b7919f9212&topFinGrpNo=020000&pageNo=1&options=intr_rate&options=intr_rate2";
//
//        //return getProductList(url);
//
//        System.out.println("------------------------- 1");

        //String str = restTemplate.getForObject(url, String.class);
        //System.out.println(str);

        List<SavingsDepositDto> list = getProductList(url);
//        for (SavingsDepositDto dto : list) {
//            System.out.println(dto);
//        }
//        System.out.println(list.toString());

        System.out.println("------------------------- 2");
        return list;
    }

    public SavingsDepositDto getSavingsByCode(String topFinGrpNo, int page, int size) throws IOException {
        String url = buildUrl(SAVINGS_API_URL, topFinGrpNo, page, size);
        return getProductDetails(url);
    }

    public SavingsDepositDto getDepositByCode(String topFinGrpNo, int page, int size) throws IOException {
        String url = buildUrl(DEPOSIT_API_URL, topFinGrpNo, page, size);
        return getProductDetails(url);
    }

    //URL 동적 생성
    private String buildUrl(String baseUrl, String topFinGrpNo, int page, int size) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("auth", AUTH_KEY)
                .queryParam("topFinGrpNo", topFinGrpNo)
                .queryParam("pageNo", page)
                .queryParam("numOfRows", size);

        return builder.build().toUriString();
    }

    private List<SavingsDepositDto> getProductList(String url) throws IOException {

        System.out.println("------------------------ getProductList 진입");
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        System.out.println(">>> getProductList");
//        System.out.println(response);
//        System.out.println(response.getBody());

        JsonNode root = objectMapper.readTree(response.getBody());
        //------------------
        JsonNode baseList = root.path("result").path("baseList");

        log.info(String.valueOf(baseList));

        return objectMapper.readValue(baseList.toString(), new TypeReference<List<SavingsDepositDto>>() {});
    }

    private SavingsDepositDto getProductDetails(String url) throws IOException {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode baseInfo = root.path("result").path("baseInfo");

        if (baseInfo.isArray() && baseInfo.size() > 0) {
            return objectMapper.treeToValue(baseInfo.get(0), SavingsDepositDto.class);
        } else if (!baseInfo.isArray()) {
            return objectMapper.treeToValue(baseInfo, SavingsDepositDto.class);
        } else {
            throw new IOException("No product details found");
        }

//        return objectMapper.treeToValue(baseInfo, SavingsDepositDto.class);
    }
}




