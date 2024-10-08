package fi.re.firebackend.controller.finance.savings;

import fi.re.firebackend.dto.finance.savings.SavingsDepositWithOptionsDto;
import fi.re.firebackend.service.savings.SavingsDepositService;
import fi.re.firebackend.util.api.SavingsDepositApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/finance") //url 변경
public class SavingsDepositController {

    private static final Logger log = LoggerFactory.getLogger(SavingsDepositController.class);
    //SavingsService 연결
    private final SavingsDepositService savingsDepositService;
    private final SavingsDepositApi savingsDepositApi;

    @Autowired
    public SavingsDepositController(SavingsDepositService savingsDepositService, SavingsDepositApi savingsDepositApi) {
        this.savingsDepositService = savingsDepositService;
        this.savingsDepositApi = savingsDepositApi;
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


    @GetMapping("/initialize")
    public ResponseEntity initializeData(
            @RequestParam(required = false) String prdtDiv) {
        try {
            savingsDepositApi.scheduledUpdate(); // 외부 API 호출 및 DB 업데이트
            return ResponseEntity.ok("Data initialization completed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during data initialization: " + e.getMessage());
        }
    }

}



//@GetMapping("/scheduleTest")
//public ResponseEntity getScheduleTestProducts(
//        @RequestParam(required = false) String prdtDiv) {
//    savingsDepositApi.scheduledUpdate();
//    return ResponseEntity.ok(null);
//}

