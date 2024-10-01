package fi.re.firebackend.service.savings;


import fi.re.firebackend.dao.finance.savings.DepositV1Dao;
import fi.re.firebackend.dao.finance.savings.SavingsDao;
import fi.re.firebackend.dao.finance.savings.SavingsV1Dao;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import fi.re.firebackend.util.api.SavingsDepositApi;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
public class SavingsDepositService {

    private static final Logger log = LoggerFactory.getLogger(SavingsDepositService.class);
    private final SavingsV1Dao savingsV1Dao;
    private final DepositV1Dao depositV1Dao;
    private final SavingsDepositApi savingsDepositApi;
    private final RestTemplate restTemplate;

    @Autowired
    public SavingsDepositService(SavingsV1Dao savingsV1Dao, DepositV1Dao depositV1Dao, SavingsDepositApi savingsDepositApi, RestTemplate restTemplate) {
        this.savingsV1Dao = savingsV1Dao;
        this.depositV1Dao = depositV1Dao;
        this.savingsDepositApi = savingsDepositApi;
        this.restTemplate = restTemplate;
    }

    //private static final List<String> TOP_FIN_GRP_NO = Arrays.asList("020000", "030200", "030300", "050000", "060000");
    private static final List<String> TOP_FIN_GRP_NO = Arrays.asList("020000","030300");

    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시에 실행
    public void updateSavingsAndDepositData() {

//        String url = "https://finlife.fss.or.kr/finlifeapi/depositProductsSearch.json?auth=775c4db3fbe78868197e38b7919f9212&topFinGrpNo=020000&pageNo=1&options=intr_rate&options=intr_rate2";
//
//        String str = restTemplate.getForObject(url, String.class);
//        System.out.println(str);

        log.info("~~~~ Updating savings and deposit");

        log.info("Starting updateSavingsAndDepositData method");
        for (String topFinGrpNo : TOP_FIN_GRP_NO) {
            log.info("Updating data for topFinGrpNo: {}", topFinGrpNo);

            updateSavingsData(topFinGrpNo);
            updateDepositData(topFinGrpNo);
        }
        log.info("Finished updateSavingsAndDepositData method");
    }


    private void updateSavingsData(String topFinGrpNo) {
        log.info("Starting updateSavingsData for topFinGrpNo: {}", topFinGrpNo);
        try {
            int page = 1;
            int size = 100;
            List<SavingsDepositDto> savingsList = new ArrayList<>();
            do {
                log.info("~~~~~~~~~~~~~~~ Fetching savings data: page {}, size {}", page, size);
                savingsList = savingsDepositApi.getAllSavings(topFinGrpNo, page, size);
                log.info("Fetched {} savings items", savingsList.size());
                for (SavingsDepositDto savings : savingsList) {
                    savingsV1Dao.insertOrUpdateSavings(savings);
                }
                page++;
            } while (!savingsList.isEmpty());
        } catch (IOException e) {
            log.error("Error updating savings data: ", e);
        }
        log.info("Finished updateSavingsData for topFinGrpNo: {}", topFinGrpNo);
    }


    private void updateDepositData(String topFinGrpNo) {
        log.info("Starting updateDepositData for topFinGrpNo: {}", topFinGrpNo);
        try {
            int page = 1;
            int size = 100;
            List<SavingsDepositDto> depositList;
            do {
                log.info("Fetching Deposit data: page {}, size {}", page, size);
                depositList = savingsDepositApi.getAllDeposit(topFinGrpNo, page, size);
                log.info("Fetched {} deposit items", depositList.size());
                for (SavingsDepositDto deposit : depositList) {
                    depositV1Dao.insertOrUpdateSavings(deposit);
                }
                page++;
            } while (!depositList.isEmpty());
        } catch (IOException e) {
            log.error("Error updating deposit data: ", e);
        }
        log.info("Finished updateDepositData for topFinGrpNo: {}", topFinGrpNo);
    }

    //페이지네이션
    public Map<String, Object> getAllProducts(int page, int size) {
        List<SavingsDepositDto> savings = savingsV1Dao.selectSavings(size, (page - 1) * size);
        List<SavingsDepositDto> deposits = depositV1Dao.selectDeposit(size, (page - 1) * size);

        System.out.println("~~~~~ getAllProducts start");
        for(SavingsDepositDto saving : savings) {
            System.out.println(saving);
        }
        for(SavingsDepositDto deposit : deposits) {
            System.out.println(deposit);
        }
        System.out.println("~~~~~ getAllProducts end");

        List<SavingsDepositDto> allProducts = new ArrayList<>();
        allProducts.addAll(savings);
        allProducts.addAll(deposits);

        int totalCount = savingsV1Dao.countSavings() + depositV1Dao.countDeposit();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("products", allProducts);
        result.put("totalCount", totalCount);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);

        return result;
        }

    }

//    public Map<String, Object> getSavings(int page, int size) {
//        List<SavingsDepositDto> savings = savingsV1Dao.selectSavings(size, (page - 1) * size);
//        int totalCount = savingsV1Dao.countSavings();
//        int totalPages = (int) Math.ceil((double) totalCount / size);
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("savings", savings);
//        result.put("totalCount", totalCount);
//        result.put("totalPages", totalPages);
//        result.put("currentPage", page);
//
//        return result;
//    }
//
//    public Map<String, Object> getDeposit(int page, int size) {
//        List<SavingsDepositDto> deposits = depositV1Dao.selectDeposit(size, (page - 1) * size);
//        int totalCount = depositV1Dao.countDeposit();
//        int totalPages = (int) Math.ceil((double) totalCount / size);
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("deposits", deposits);
//        result.put("totalCount", totalCount);
//        result.put("totalPages", totalPages);
//        result.put("currentPage", page);
//
//        return result;
//    }

