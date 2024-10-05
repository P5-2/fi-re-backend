package fi.re.firebackend.service.savings;



import fi.re.firebackend.dao.finance.savings.SavingsDepositDao;
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
    public SavingsDepositWithOptionsDto getProductDetail(String finPrdtCd, String saveTrm) {
        return savingsDepositDao.getProductDetail(finPrdtCd, saveTrm);
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


    // DB에서 페이지네이션 적용하여 데이터 조회
    public Map<String, Object> getAllProducts(int page, int size, String productType) {
        String prdtDiv = convertProductType(productType);
        int offset = (page - 1) * size;
        List<SavingsDepositWithOptionsDto> products;
        int totalCount;

        if (prdtDiv == null || prdtDiv.isEmpty()) {
            // 예금, 적금 모두 가져오기
            products = savingsDepositDao.getAllProducts(offset, size, null);
            totalCount = savingsDepositDao.getTotalProductCount(null);
        } else {
            // 특정 상품 유형만 가져오기
            products = savingsDepositDao.getAllProducts(offset, size, prdtDiv);
            totalCount = savingsDepositDao.getTotalProductCount(prdtDiv);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("totalCount", totalCount);
        result.put("totalPages", (int) Math.ceil((double) totalCount / size));
        result.put("currentPage", page);
        return result;
    }

    // 예금, 적금 타입 변환
    private String convertProductType(String productType) {
        if (productType == null || productType.isEmpty()) {
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
}




//주기적인 DB 업데이트를 위함
//  @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
//public void scheduledUpdate() {
//    try {
//        log.info("Starting scheduled update...");
//        updateAllProducts();
//        log.info("Scheduled update completed.");
//    } catch (Exception e) {
//        log.error("Error during scheduled update: ", e);
//    }
//}
//
//// insert / update 처리
//
//public void updateAllProducts() {
//    try {
//        List<SavingsDepositWithOptionsDto> savingsProducts = fetchAllProducts(SAVINGS_API_URL);
//        List<SavingsDepositWithOptionsDto> depositProducts = fetchAllProducts(DEPOSIT_API_URL);
//
//        updateDatabase(savingsProducts);
//        updateDatabase(depositProducts);
//    } catch (Exception e) {
//        log.error("Error updating all products: ", e);
//        throw new RuntimeException("Failed to update products", e);
//    }
//}
