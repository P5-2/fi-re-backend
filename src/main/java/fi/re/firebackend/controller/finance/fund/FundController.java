package fi.re.firebackend.controller.finance.fund;

import fi.re.firebackend.dao.finance.fund.FundDao;
import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.service.fund.FundService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/finance/fund")
public class FundController {
    private FundDao fundDao;
    private final FundService fundService;

    public FundController(FundDao fundDao, FundService fundService) {
        this.fundDao = fundDao;
        this.fundService = fundService;
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

    @GetMapping("/pageAll")
    public Map<String, Object> getFunds(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        System.out.println("FundController getFunds()");
        List<FundDto> funds = fundService.getFunds(page, size);
        int totalCount = fundService.getTotalCount();

        Map<String, Object> response = new HashMap<>();
        response.put("funds", funds);
        response.put("totalCount", totalCount);
        response.put("totalPages", (int) Math.ceil((double) totalCount / size));
        response.put("currentPage", page);

        return response;
    }
}
