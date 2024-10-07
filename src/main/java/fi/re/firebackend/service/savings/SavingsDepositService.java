package fi.re.firebackend.service.savings;



import com.fasterxml.jackson.core.JsonProcessingException;
import fi.re.firebackend.dao.finance.savings.SavingsDepositDao;
import fi.re.firebackend.dto.finance.savings.AllPageListDto;
import fi.re.firebackend.dto.finance.savings.OptionalDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositWithOptionsDto;
import fi.re.firebackend.util.api.SavingsDepositApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
public class SavingsDepositService {

    private static final Logger log = LoggerFactory.getLogger(SavingsDepositService.class);
    private final SavingsDepositApi savingsDepositApi;
    private final SavingsDepositDao savingsDepositDao;

    @Autowired
    public SavingsDepositService(SavingsDepositApi savingsDepositApi, SavingsDepositDao savingsDepositDao) {
        this.savingsDepositApi = savingsDepositApi;
        this.savingsDepositDao = savingsDepositDao;
    }

    //예적금 상세페이지 가져오기
    public SavingsDepositWithOptionsDto getProductDetail(String finPrdtCd, String saveTrm, String intrRateTypeNm) {
        return savingsDepositDao.getProductDetail(finPrdtCd, saveTrm, intrRateTypeNm);
    }

    //예적금 Hot 리스트 가져오기
    public List<SavingsDepositWithOptionsDto> getHotProducts(String prdtDiv) {
        return savingsDepositDao.getHotProducts(prdtDiv, 3);
    }



    @Transactional
    public void updateDatabase(List<SavingsDepositWithOptionsDto> products) {
        for (SavingsDepositWithOptionsDto product : products) {
            if (savingsDepositDao.checkProductExists(product.getSavingsDeposit().getFinPrdtCd())) {
                savingsDepositDao.updateProduct(product);
                savingsDepositDao.deleteProductOptions(product.getSavingsDeposit().getFinPrdtCd());
                savingsDepositDao.insertUpdatedProductOptions(product);
            } else {
                savingsDepositDao.insertProduct(product);
                savingsDepositDao.insertProductOptions(product);
            }
        }
    }

    // 검색어 ->
    public Map<String, Object> getSavingsDepositPageList(AllPageListDto dto){
        System.out.println("~~~ getSavingsDepositPageList");
        int pageNumber = 0;
        if(dto != null){
            pageNumber = dto.getPageNumber();
            System.out.println(dto.toString());
        }

        List<SavingsDepositWithOptionsDto> list = savingsDepositDao.getSavingsDepositPageList(dto);
        for (SavingsDepositWithOptionsDto s: list){
            System.out.println(s.toString());
        }

        int totalItems = savingsDepositDao.getTotalProductCount(dto);
        System.out.println("~~~ totalItems:" + totalItems);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalCount", totalItems);
        result.put("totalPages", (int) Math.ceil((double) totalItems / 5));
        result.put("pageNumber", pageNumber);
        System.out.println("~~~ pageNumber:" + pageNumber);

        return result;
    }


    // DB에서 페이지네이션 적용하여 데이터 조회
    /*public Map<String, Object> getAllProducts(int page, int size, String productType) {

        // allPageList
        // dto -> int page, int size, String productType

        List<SavingsDepositWithOptionsDto> allProducts = savingsDepositDao.getAllProducts(new AllPageListDto(page, size, productType));


        List<SavingsDepositWithOptionsDto> allProducts = new ArrayList<>();
        String prdtDiv = convertProductType(productType);

        try {
            if (prdtDiv == null) {
                allProducts.addAll(savingsDepositApi.fetchAllProducts(savingsDepositApi.SAVINGS_API_URL));
                allProducts.addAll(savingsDepositApi.fetchAllProducts(savingsDepositApi.DEPOSIT_API_URL));
            } else if ("S".equals(prdtDiv)) {
                allProducts.addAll(savingsDepositApi.fetchAllProducts(savingsDepositApi.SAVINGS_API_URL));
            } else if ("D".equals(prdtDiv)) {
                allProducts.addAll(savingsDepositApi.fetchAllProducts(savingsDepositApi.DEPOSIT_API_URL));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing JSON data", e);
        }

//        List<SavingsDepositWithOptionsDto> filteredProducts = filterOptions(allProducts);

        int totalItems = allProducts.size();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, totalItems);
        List<SavingsDepositWithOptionsDto> paginatedProducts = allProducts.subList(startIndex, endIndex);

        Map<String, Object> result = new HashMap<>();
        result.put("products", paginatedProducts);
        result.put("totalCount", totalItems);
        result.put("totalPages", (int) Math.ceil((double) totalItems / size));
        result.put("currentPage", page);

        return result;
    }*/

    // 예금, 적금 타입 변환
    private String convertProductType(String productType) {
        if (productType == null || productType.equals("all")|| productType.isEmpty()) {
            return null; // 모든 상품 조회
        }
        switch (productType.toLowerCase()) {
            case "savings":
                return "S"; // 적금
            case "deposit":
                return "D"; // 예금
            default:
                throw new IllegalArgumentException("Invalid product type: " + productType);
        }
    }

    //전체 리스트로 받아올 때 단리, 12개월 기준으로 높은 우대금리 가져오는 옵션을 필터링 하는 기능
//    public List<SavingsDepositWithOptionsDto> filterOptions(List<SavingsDepositWithOptionsDto> products) {
//        List<SavingsDepositWithOptionsDto> filteredProducts = new ArrayList<>();
//
//        for (SavingsDepositWithOptionsDto product : products) {
//            SavingsDepositDto savingsDeposit = product.getSavingsDeposit();
//            List<OptionalDto> options = product.getOptions();
//
//            OptionalDto bestOption = options.stream()
//                    .filter(o -> "단리".equals(o.getIntrRateTypeNm())) // 단리 필터링
//                    .filter(o -> "12".equals(o.getSaveTrm())) // 12개월 필터링
//                    .max(Comparator.comparingDouble(o -> Double.parseDouble(o.getIntrRate2()))) // 최고 금리 선택
//                    .orElse(null);
//
//            if (bestOption != null) {
//                filteredProducts.add(new SavingsDepositWithOptionsDto(savingsDeposit, Collections.singletonList(bestOption)));
//            }
//        }
//
//        return filteredProducts;
//    }
}


