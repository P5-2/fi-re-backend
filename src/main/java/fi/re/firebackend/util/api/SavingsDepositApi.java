package fi.re.firebackend.util.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.re.firebackend.dao.finance.savings.SavingsDepositDao;
import fi.re.firebackend.dto.finance.savings.OptionalDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;

import fi.re.firebackend.dto.finance.savings.SavingsDepositWithOptionsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
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
    private final SavingsDepositDao savingsDepositDao;

    //Json처리와 HTTP요청
    @Autowired
    private SavingsDepositApi(ObjectMapper objectMapper, RestTemplate restTemplate, SavingsDepositDao savingsDepositDao) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.savingsDepositDao = savingsDepositDao;
    }

    //초기 데이터 삽입
    @PostConstruct
    public void initializeData() {
        try {
            log.info("Initializing data...");
            updateAllProducts();
            log.info("Data initialization completed.");
        } catch (Exception e) {
            log.error("Error initializing data: ", e);
            // 초기화 실패 시 추가적인 처리를 할 수 있습니다.
        }
    }

    //주기적인 DB 업데이트를 위함
    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
    public void scheduledUpdate() {
        try {
            log.info("Starting scheduled update...");
            updateAllProducts();
            log.info("Scheduled update completed.");
        } catch (Exception e) {
            log.error("Error during scheduled update: ", e);
        }
    }

    // insert / update 처리
    public void updateAllProducts() {
        try {
            List<SavingsDepositWithOptionsDto> savingsProducts = fetchSavingsProducts();
            List<SavingsDepositWithOptionsDto> depositProducts = fetchDepositProducts();

            updateDatabase(savingsProducts);
            updateDatabase(depositProducts);
        } catch (Exception e) {
            log.error("Error updating all products: ", e);
            throw new RuntimeException("Failed to update products", e);
        }
    }

    private List<SavingsDepositWithOptionsDto> fetchSavingsProducts() {
        log.info("Fetching savings products...");
        String url = UriComponentsBuilder.fromHttpUrl(SAVINGS_API_URL)
                .queryParam("auth", AUTH_KEY)
                .queryParam("topFinGrpNo", "020000") // 권역코드 (예시)
                .queryParam("pageNo", "1") // 페이지 번호
                .build()
                .toUriString();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return parseApiResponse(response.getBody());
    }

    private List<SavingsDepositWithOptionsDto> fetchDepositProducts() {
        log.info("Fetching deposit products...");
        String url = UriComponentsBuilder.fromHttpUrl(DEPOSIT_API_URL)
                .queryParam("auth", AUTH_KEY)
                .queryParam("topFinGrpNo", "020000") // 권역코드 (예시)
                .queryParam("pageNo", "1") // 페이지 번호
                .build()
                .toUriString();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return parseApiResponse(response.getBody());
    }

//    //적금 API 호출
//    private List<SavingsDepositWithOptionsDto> fetchSavingsProducts() {
//        log.info("Fetching savings products...");
//        ResponseEntity<String> response = restTemplate.getForEntity(SAVINGS_API_URL + "?auth=" + AUTH_KEY, String.class);
//        return parseApiResponse(response.getBody());
//    }
//
//    //예금 API 호출
//    private List<SavingsDepositWithOptionsDto> fetchDepositProducts() {
//        log.info("Fetching deposit products...");
//        ResponseEntity<String> response = restTemplate.getForEntity(DEPOSIT_API_URL + "?auth=" + AUTH_KEY, String.class);
//        return parseApiResponse(response.getBody());
//    }

    //API 응답을 DTO 객체로 변환
    private List<SavingsDepositWithOptionsDto> parseApiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode dataNode = root.path("result").path("baseList");
            List<SavingsDepositWithOptionsDto> savingsDepositproducts = new ArrayList<>();

            for (JsonNode productNode : dataNode) {
                SavingsDepositDto savingsDepositproduct = objectMapper.treeToValue(productNode, SavingsDepositDto.class);
                List<OptionalDto> options = new ArrayList<>();

                JsonNode optionsNode = root.path("result").path("optionList");
                for (JsonNode optionNode : optionsNode) {
                    if (optionNode.path("fin_prdt_cd").asText().equals(savingsDepositproduct.getFinPrdtCd())) {
                        OptionalDto option = objectMapper.treeToValue(optionNode, OptionalDto.class);
                        options.add(option);
                    }
                }

                savingsDepositproducts.add(new SavingsDepositWithOptionsDto(savingsDepositproduct, options));
            }

            return savingsDepositproducts;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing API response", e);
        }
    }


    @Transactional
    public void updateDatabase(List<SavingsDepositWithOptionsDto> savingsDepositproducts) {
        for (SavingsDepositWithOptionsDto savingsDeposit : savingsDepositproducts) {
            try {
                if (savingsDepositDao.checkProductExists(savingsDeposit.getSavingsDeposit().getFinPrdtCd())) {
                    savingsDepositDao.updateProduct(savingsDeposit);
                    log.info("Updated product: {}", savingsDeposit.getSavingsDeposit().getFinPrdtCd());
                } else {
                    savingsDepositDao.insertProduct(savingsDeposit);
                    log.info("Inserted new product: {}", savingsDeposit.getSavingsDeposit().getFinPrdtCd());
                }
            } catch (Exception e) {
                log.error("Error updating/inserting product: {}", savingsDeposit.getSavingsDeposit().getFinPrdtCd(), e);
            }
        }
    }
}




//    //적금 상품 목록 조회
//    public List<SavingsDepositDto> getAllSavings(String topFinGrpNo, int page, int size) throws IOException {
//        String url = buildUrl(SAVINGS_API_URL, topFinGrpNo, page, size);
//
//        return getProductList(url);
//    }
//
//    //예금 상품 목록 조회
//    public List<SavingsDepositDto> getAllDeposit(String topFinGrpNo, int page, int size) throws IOException {
//        String url = buildUrl(DEPOSIT_API_URL, topFinGrpNo, page, size);
////        String url = "https://finlife.fss.or.kr/finlifeapi/depositProductsSearch.json?auth=775c4db3fbe78868197e38b7919f9212&topFinGrpNo=020000&pageNo=1&options=intr_rate&options=intr_rate2";
////
////        //return getProductList(url);
////
////        System.out.println("------------------------- 1");
//
//        //String str = restTemplate.getForObject(url, String.class);
//        //System.out.println(str);
//
//        List<SavingsDepositDto> list = getProductList(url);
////        for (SavingsDepositDto dto : list) {
////            System.out.println(dto);
////        }
////        System.out.println(list.toString());
//
//        System.out.println("------------------------- 2");
//        return list;
//    }

//    //URL 동적 생성
//    private String buildUrl(String baseUrl, String topFinGrpNo, int page, int size) {
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
//                .queryParam("auth", AUTH_KEY)
//                .queryParam("topFinGrpNo", topFinGrpNo)
//                .queryParam("pageNo", page)
//                .queryParam("numOfRows", size);
//
//        return builder.build().toUriString();
//    }
//
//    private List<SavingsDepositDto> getProductList(String url) throws IOException {
//
//        System.out.println("------------------------ getProductList 진입");
//        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//
//        System.out.println(">>> getProductList");
////        System.out.println(response);
////        System.out.println(response.getBody());
//
//        JsonNode root = objectMapper.readTree(response.getBody());
//        //------------------
//        JsonNode baseList = root.path("result").path("baseList");
//
//        log.info(String.valueOf(baseList));
//
//        return objectMapper.readValue(baseList.toString(), new TypeReference<List<SavingsDepositDto>>() {});
//    }
//
//    private SavingsDepositDto getProductDetails(String url) throws IOException {
//        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//        JsonNode root = objectMapper.readTree(response.getBody());
//        JsonNode baseInfo = root.path("result").path("baseInfo");
//
//        if (baseInfo.isArray() && baseInfo.size() > 0) {
//            return objectMapper.treeToValue(baseInfo.get(0), SavingsDepositDto.class);
//        } else if (!baseInfo.isArray()) {
//            return objectMapper.treeToValue(baseInfo, SavingsDepositDto.class);
//        } else {
//            throw new IOException("No product details found");
//        }
//
////        return objectMapper.treeToValue(baseInfo, SavingsDepositDto.class);
//    }
//}




