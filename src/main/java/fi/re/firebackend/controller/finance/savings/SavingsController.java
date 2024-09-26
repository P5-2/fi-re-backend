package fi.re.firebackend.controller.finance.savings;

import fi.re.firebackend.dao.finance.savings.SavingsDao;
import fi.re.firebackend.dto.finance.savings.SavingsDto;
import fi.re.firebackend.service.savings.SavingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/finance/savings")
public class SavingsController {
    private SavingsDao savingsDao;

    //SavingsService 연결
    private final SavingsService savingsService;

    @Autowired
    public SavingsController(SavingsDao savingsDao, SavingsService savingsService) {
        this.savingsDao = savingsDao;
        this.savingsService = savingsService;
    }

    @GetMapping("/get")
    public SavingsDto getSavings(int prdNo) {
        System.out.println("prdNo : "+prdNo);
        return savingsDao.getSavingsById(prdNo);
    }

    @GetMapping("/hot")
    public List<SavingsDto> getHotSavings() {
        System.out.println("SavingsController getHotSavings()");
        return savingsDao.hot();
    }

    @GetMapping("/all")
    public List<SavingsDto> getAllSavings() {
        System.out.println("SavingsController getAllSavings()");
        return savingsDao.all();
    }
}
