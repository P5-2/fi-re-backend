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
    public List<SavingsDepositWithOptionsDto> getProductDetail(String finPrdtCd, String intrRateTypeNm, String rsrvType) {
        return savingsDepositDao.getProductDetail(finPrdtCd, intrRateTypeNm, rsrvType);
    }

    public int plusSelectCount(String finPrdtCd){
        return savingsDepositDao.plusSelectCount(finPrdtCd);
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

    // 페이지네이션
    public Map<String, Object> getSavingsDepositPageList(AllPageListDto dto){
        int pageNumber = 0;
        if(dto != null){
            pageNumber = dto.getPageNumber();
            log.info(dto.toString());
        }

        List<SavingsDepositWithOptionsDto> list = savingsDepositDao.getSavingsDepositPageList(dto);
        for (SavingsDepositWithOptionsDto s: list){
            log.info(s.toString());
        }

        int totalItems = savingsDepositDao.getTotalProductCount(dto);
        log.info("~~~ totalItems:" + totalItems);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalCount", totalItems);
        result.put("totalPages", (int) Math.ceil((double) totalItems / 5));
        result.put("pageNumber", pageNumber);
        log.info("~~~ pageNumber:" + pageNumber);

        return result;
    }

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
}


