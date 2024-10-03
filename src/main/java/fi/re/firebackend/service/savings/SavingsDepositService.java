package fi.re.firebackend.service.savings;



import fi.re.firebackend.dao.finance.savings.SavingsDepositDao;
import fi.re.firebackend.dto.finance.savings.SavingsDepositWithOptionsDto;
import fi.re.firebackend.util.api.SavingsDepositApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Component
public class SavingsDepositService {

    private final SavingsDepositApi savingsDepositApi;
    private final SavingsDepositDao savingsDepositDao;

    @Autowired
    public SavingsDepositService(SavingsDepositApi savingsDepositApi, SavingsDepositDao savingsDepositDao) {
        this.savingsDepositApi = savingsDepositApi;
        this.savingsDepositDao = savingsDepositDao;
    }


    //예적금 상세페이지 가져오기
    public SavingsDepositWithOptionsDto getProductDetail(String finPrdtCd) {
        return savingsDepositDao.getProductDetail(finPrdtCd);
    }

    //예적금 Hot 리스트 가져오기
    public List<SavingsDepositWithOptionsDto> getHotProducts(String prdtDiv) {
        return savingsDepositDao.getHotProducts(prdtDiv, 3);
    }

    //예적금 전체 페이지 가져오기(페이지 네이션 포함)
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

    //예금,적금 타입 변환
    private String convertProductType(String productType) {
        if (productType == null || productType.isEmpty()) {
            return null;
        }
        return productType.equalsIgnoreCase("savings") ? "S" : "D";
    }
}


////private static final List<String> TOP_FIN_GRP_NO = Arrays.asList("020000", "030200", "030300", "050000", "060000");
//private static final List<String> TOP_FIN_GRP_NO = Arrays.asList("020000", "030300");
////        String url = "https://finlife.fss.or.kr/finlifeapi/depositProductsSearch.json?auth=775c4db3fbe78868197e38b7919f9212&topFinGrpNo=020000&pageNo=1&options=intr_rate&options=intr_rate2";
////
////        String str = restTemplate.getForObject(url, String.class);
////        System.out.println(str);
