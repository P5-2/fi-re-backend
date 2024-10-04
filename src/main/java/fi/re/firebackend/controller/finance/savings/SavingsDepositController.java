package fi.re.firebackend.controller.finance.savings;

import fi.re.firebackend.dao.finance.savings.DepositV1Dao;
import fi.re.firebackend.dao.finance.savings.SavingsV1Dao;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import fi.re.firebackend.service.savings.SavingsDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/finance/savingsDeposit") //url 변경
public class SavingsDepositController {
    private SavingsV1Dao savingsV1Dao;
    private DepositV1Dao depositV1Dao;

    //SavingsService 연결
    private final SavingsDepositService savingsDepositService;

    @Autowired
    public SavingsDepositController(SavingsV1Dao savingsV1Dao, DepositV1Dao depositV1Dao, SavingsDepositService savingsDepositService) {
        this.savingsV1Dao = savingsV1Dao;
        this.depositV1Dao = depositV1Dao;
        this.savingsDepositService = savingsDepositService;
    }

    @GetMapping("/savings/get")
    public SavingsDepositDto getSavingsByCode(@RequestParam String finPrdtCd) {
        System.out.println("finPrdt : "+finPrdtCd);
        return savingsV1Dao.getSavingsByCode(finPrdtCd);
    }

    @GetMapping("/savings/hot")
    public List<SavingsDepositDto> getHotSavings() {
        System.out.println("SavingsController getHotSavings()");
        return savingsV1Dao.getHotSavings();
    }

    @GetMapping("/savings/all")
    public List<SavingsDepositDto> getAllSavings() {
        System.out.println("SavingsController getAllSavings()");
        return savingsV1Dao.getAllSavings();
    }

//    @GetMapping("/savings/pageAll")
//    public Map<String, Object> getSavings(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        System.out.println("SavingsController getSavings()");
//        List<SavingsDepositDto> savings = savingsDepositService.getSavings(page, size);
//        int totalCount = savingsDepositService.getTotalSavingsCount();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("savings", savings);
//        response.put("totalCount", totalCount);
//        response.put("totalPages", (int) Math.ceil((double) totalCount / size));
//        response.put("currentPage", page);
//
//        return response;
//    }
    @GetMapping("/deposit/get")
    public SavingsDepositDto getDepositByCode(@RequestParam String finPrdtCd) {
        System.out.println("finPrdtCd : "+finPrdtCd);
        return depositV1Dao.getDepositByCode(finPrdtCd);
    }

    @GetMapping("/deposit/hot")
    public List<SavingsDepositDto> getHotDeposit() {
        System.out.println("SavingsController getHotDeposit()");
        return depositV1Dao.getHotDeposit();
    }

    @GetMapping("/deposit/all")
    public List<SavingsDepositDto> getAllDeposit() {
        System.out.println("SavingsController getAllDeposit()");
        return depositV1Dao.getAllDeposit();
    }

    @GetMapping("/deposit/pageAll")
    public Map<String, Object> getDeposit(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        System.out.println("SavingsController getDeposit()");
        List<SavingsDepositDto> deposit = savingsDepositService.getDeposit(page, size);
        int totalCount = savingsDepositService.getTotalDepositCount();

        Map<String, Object> response = new HashMap<>();
        response.put("deposit", deposit);
        response.put("totalCount", totalCount);
        response.put("totalPages", (int) Math.ceil((double) totalCount / size));
        response.put("currentPage", page);

        return response;
    }

}
