package fi.re.firebackend.controller.finance.savings;

import fi.re.firebackend.dao.finance.savings.SavingsDao;
import fi.re.firebackend.dto.finance.savings.SavingsDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/finance/savings")
public class SavingsController {
    private SavingsDao savingsDao;

    public SavingsController(SavingsDao savingsDao) {
        this.savingsDao = savingsDao;
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
