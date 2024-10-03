package fi.re.firebackend.controller.finance.savings;

import fi.re.firebackend.dto.finance.savings.SavingsDepositWithOptionsDto;
import fi.re.firebackend.service.savings.SavingsDepositService;
import fi.re.firebackend.util.api.SavingsDepositApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/finance") //url 변경
public class SavingsDepositController {

    //SavingsService 연결
    private final SavingsDepositService savingsDepositService;

    @Autowired
    public SavingsDepositController(SavingsDepositService savingsDepositService) {
        this.savingsDepositService = savingsDepositService;
    }

    //예적금 상세페이지
    @GetMapping("/{finPrdtCd}")
    public ResponseEntity<SavingsDepositWithOptionsDto> getProductDetail(@PathVariable String finPrdtCd) {
        return ResponseEntity.ok(savingsDepositService.getProductDetail(finPrdtCd));
    }
    //Hot3 list
    @GetMapping("/hot")
    public ResponseEntity<List<SavingsDepositWithOptionsDto>> getHotProducts(
            @RequestParam(required = false) String prdtDiv) {
        return ResponseEntity.ok(savingsDepositService.getHotProducts(prdtDiv));
    }

    //페이지네이션(모든 상품 가져오기)
    @GetMapping("/pageAll")
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String productType) {
        return ResponseEntity.ok(savingsDepositService.getAllProducts(page, size, productType));
    }
}


//    @GetMapping("/savings/get")
//    public SavingsDepositDto getSavingsByCode(@RequestParam String finPrdtCd) {
//        System.out.println("finPrdt : "+finPrdtCd);
//        return savingsV1Dao.getSavingsByCode(finPrdtCd);
//    }


//    @GetMapping("/savings/hot")
//    public List<SavingsDepositDto> getHotSavings() {
//        System.out.println("SavingsController getHotSavings()");
//        return savingsV1Dao.getHotSavings();
//    }


//    @GetMapping("/pageAll")
//    public ResponseEntity<Map<String, Object>> getAllProducts(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "5") int size) {
//        savingsDepositService.updateSavingsAndDepositData();
//        Map<String, Object> response = savingsDepositService.getAllProducts(page, size);
//        return ResponseEntity.ok(response);
//    }

//    @GetMapping("/deposit/pageAll")
//    public Map<String, Object> getDeposit(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "5") int size) {
//        return savingsDepositService.getDeposit(page, size);
//    }


