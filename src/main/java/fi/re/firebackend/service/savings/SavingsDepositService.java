package fi.re.firebackend.service.savings;


import fi.re.firebackend.dao.finance.savings.DepositV1Dao;
import fi.re.firebackend.dao.finance.savings.SavingsDao;
import fi.re.firebackend.dao.finance.savings.SavingsV1Dao;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SavingsDepositService {
    private final RestTemplate restTemplate;
    private final SavingsV1Dao savingsV1Dao;
    private final DepositV1Dao depositV1Dao;

    @Autowired
    public SavingsDepositService(RestTemplate restTemplate, SavingsDao savingsDao, SavingsV1Dao savingsV1Dao, DepositV1Dao depositV1Dao) {
        this.restTemplate = restTemplate;
        this.savingsV1Dao = savingsV1Dao;
        this.depositV1Dao = depositV1Dao;
    }

//
//    public List<SavingsDepositDto> getSavings(int page, int size) {
//        int offset = (page - 1) * size;
//        return SavingsV1Dao.selectSavings(size, offset);
//    }

    public int getTotalSavingsCount() {
        return savingsV1Dao.countSavings();
    }


    public List<SavingsDepositDto> getDeposit(int page, int size) {
        int offset = (page - 1) * size;
        return depositV1Dao.selectDeposit(size, offset);
    }

    public int getTotalDepositCount() {
        return depositV1Dao.countDeposit();
    }



}
