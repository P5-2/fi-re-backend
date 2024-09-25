package fi.re.firebackend.controller.recommendation;

import fi.re.firebackend.dto.recommendation.filtering.DepositVo;
import fi.re.firebackend.dto.recommendation.filtering.FundVo;
import fi.re.firebackend.service.recommendation.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommend")
public class RecommendationController {

    private final RecommendationService recmdService;

    public RecommendationController(RecommendationService recmdService) {
        this.recmdService = recmdService;
    }

    @GetMapping("/deposit")
    public ResponseEntity<List<DepositVo>> getDepositRecmd(@RequestParam String username) {
        List<DepositVo> recommendedDeposits = recmdService.getRecmdedDeposits(username);
        return new ResponseEntity<>(recommendedDeposits, HttpStatus.OK);
    }


    // 펀드 상품 추천 URL
    // 필터링 되어 선정된 몇 개의 펀드를 담아 반환
    @GetMapping("/fund")
    public ResponseEntity<List<FundVo>> getFundRecmd(@RequestParam String username) {
        List<FundVo> recommendedFunds = recmdService.getRecmdedFunds(username);
        return new ResponseEntity<>(recommendedFunds, HttpStatus.OK);
    }
}
