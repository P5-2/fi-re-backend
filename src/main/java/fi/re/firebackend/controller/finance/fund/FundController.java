package fi.re.firebackend.controller.finance.fund;

import fi.re.firebackend.dao.finance.fund.FundDao;
import fi.re.firebackend.dto.finance.fund.FundDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/finance/fund")
public class FundController {
    private FundDao fundDao;

    public FundController(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    @GetMapping("/get")
    public FundDto getFund(int prdNo) {
        System.out.println("FundController getFund()");
        return fundDao.getFundById(prdNo);
    }

    @GetMapping("/hot")
    public List<FundDto> getHotFund() {
        System.out.println("FundController getHotFund()");
        return fundDao.hot();
    }

    @GetMapping("/all")
    public List<FundDto> getAllFund() {
        System.out.println("FundController getAllFund()");
        return fundDao.all();
    }
}
