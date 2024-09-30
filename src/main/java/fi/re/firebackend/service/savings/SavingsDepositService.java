package fi.re.firebackend.service.savings;


import fi.re.firebackend.dao.finance.savings.DepositV1Dao;
import fi.re.firebackend.dao.finance.savings.SavingsDao;
import fi.re.firebackend.dao.finance.savings.SavingsV1Dao;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import fi.re.firebackend.util.api.SavingsDepositApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SavingsDepositService {
    private final SavingsV1Dao savingsV1Dao;
    private final DepositV1Dao depositV1Dao;
    private final SavingsDepositApi savingsDepositApi;

    @Autowired
    public SavingsDepositService(SavingsV1Dao savingsV1Dao, DepositV1Dao depositV1Dao, SavingsDepositApi savingsDepositApi) {
        this.savingsV1Dao = savingsV1Dao;
        this.depositV1Dao = depositV1Dao;
        this.savingsDepositApi = savingsDepositApi;
    }

//    페이지네이션
    public Map<String, Object> getSavings(int page, int size) {
        List<SavingsDepositDto> savings = savingsV1Dao.selectSavings(size, (page - 1) * size);
        int totalCount = savingsV1Dao.countSavings();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("savings", savings);
        result.put("totalCount", totalCount);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);

        return result;
    }

    public Map<String, Object> getDeposit(int page, int size) {
        List<SavingsDepositDto> deposits = depositV1Dao.selectDeposit(size, (page - 1) * size);
        int totalCount = depositV1Dao.countDeposit();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("deposits", deposits);
        result.put("totalCount", totalCount);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);

        return result;
    }
}
