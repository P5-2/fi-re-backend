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

    // 주기적인 DB 업데이트를 위한 스케줄링 메서드
    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시에 실행
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
            List<SavingsDepositWithOptionsDto> savingsProducts = fetchAllProducts(SAVINGS_API_URL);
            List<SavingsDepositWithOptionsDto> depositProducts = fetchAllProducts(DEPOSIT_API_URL);

            updateDatabase(savingsProducts);
            updateDatabase(depositProducts);
        } catch (Exception e) {
            log.error("Error updating all products: ", e);
            throw new RuntimeException("Failed to update products", e);
        }
    }


    // 모든 권역코드와 페이지에서 상품을 가져오는 메서드
    public List<SavingsDepositWithOptionsDto> fetchAllProducts(String apiUrl) throws JsonProcessingException {
        List<SavingsDepositWithOptionsDto> allProducts = new ArrayList<>();
        String[] regions = {"020000", "030300"}; // 예시 권역코드 020000(은행), 030200(여신전문), 030300(저축은행), 050000(보험), 060000(금융투자)

        for (String region : regions) {
            int page = 1;
            boolean hasMorePages = true;
            while (hasMorePages) {
                String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                        .queryParam("auth", AUTH_KEY)
                        .queryParam("topFinGrpNo", region)
                        .queryParam("pageNo", page)
                        .build()
                        .toUriString();
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                List<SavingsDepositWithOptionsDto> products = parseApiResponse(response.getBody());
                allProducts.addAll(products);

                // Check if more pages exist
                if (products.isEmpty()) {
                    hasMorePages = false;
                } else {
                    page++;
                }
            }
        }
        return allProducts;
    }

    //API 응답을 DTO 객체로 변환
    private List<SavingsDepositWithOptionsDto> parseApiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode baseList = root.path("result").path("baseList");
            JsonNode optionList = root.path("result").path("optionList");

            List<SavingsDepositWithOptionsDto> savingsProducts = new ArrayList<>();

            for (JsonNode productNode : baseList) {
                SavingsDepositDto savingsProduct = objectMapper.treeToValue(productNode, SavingsDepositDto.class);
                savingsProduct.setPrdtDiv(root.path("result").path("prdt_div").asText()); // prdt_div 설정
                List<OptionalDto> options = new ArrayList<>();

                for (JsonNode optionNode : optionList) {
                    if (optionNode.path("fin_prdt_cd").asText().equals(savingsProduct.getFinPrdtCd())) {
                        OptionalDto option = objectMapper.treeToValue(optionNode, OptionalDto.class);
                        options.add(option);
                    }
                }
                savingsProducts.add(new SavingsDepositWithOptionsDto(savingsProduct, options));
            }
            return savingsProducts;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing API response", e);
        }
    }

    @Transactional
    public void updateDatabase(List<SavingsDepositWithOptionsDto> savingsDepositproducts) {
        for (SavingsDepositWithOptionsDto savingsDeposit : savingsDepositproducts) {
            try {
                if (savingsDepositDao.checkProductExists(savingsDeposit.getSavingsDeposit().getFinPrdtCd())) {
                    savingsDepositDao.updateProduct(savingsDeposit);
                    savingsDepositDao.deleteProductOptions(savingsDeposit.getSavingsDeposit().getFinPrdtCd());
                    savingsDepositDao.insertUpdatedProductOptions(savingsDeposit);
                    log.info("Updated product: {}", savingsDeposit.getSavingsDeposit().getFinPrdtCd());
                } else {
                    savingsDepositDao.insertProduct(savingsDeposit);
                    savingsDepositDao.insertProductOptions(savingsDeposit);
                    log.info("Inserting {} options for product: {}", savingsDeposit.getOptions().size(), savingsDeposit.getSavingsDeposit().getFinPrdtCd());
                    log.info("Inserted new product: {}", savingsDeposit.getSavingsDeposit().getFinPrdtCd());
                }
            } catch (Exception e) {
                log.error("Error updating/inserting product: {}", savingsDeposit.getSavingsDeposit().getFinPrdtCd(), e);
            }
        }
    }
}

//for (String region : regions) {
//int page = 1;
//int EXPECTED_PAGE_SIZE = 5;
//boolean hasMorePages = true;
//            while (hasMorePages) {
//String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
//        .queryParam("auth", AUTH_KEY)
//        .queryParam("topFinGrpNo", region)
//        .queryParam("pageNo", page)
//        .build()
//        .toUriString();
//ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//List<SavingsDepositWithOptionsDto> products = parseApiResponse(response.getBody());
//                allProducts.addAll(products);
//
//// Check if more pages exist
//                if (products.isEmpty() || products.size() < EXPECTED_PAGE_SIZE) {
//hasMorePages = false;
//        } else {
//page++;
//        }
//        }
//        }
//        return allProducts;
//    }


