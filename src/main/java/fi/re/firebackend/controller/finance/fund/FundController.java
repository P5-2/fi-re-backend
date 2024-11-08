package fi.re.firebackend.controller.finance.fund;

import fi.re.firebackend.dao.finance.fund.FundDao;
import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.service.fund.FundService;
import fi.re.firebackend.service.profile.ProfileService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

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
        return fundDao.getFundById(prdNo);
    }

    @GetMapping("/count")
    public boolean plusSelectCount(int prdNo){
        return fundDao.plusSelectCount(prdNo) > 0;
    }

    @GetMapping("/hot")
    public List<FundDto> getHotFund() {
        return fundDao.hot();
    }

    @GetMapping("/all")
    public List<FundDto> getAllFund() {
        return fundDao.all();
    }

    @GetMapping("/pageAll")
    public Map<String, Object> getFunds(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
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
