package fi.re.firebackend.controller.recommendation;

import fi.re.firebackend.dto.recommendation.filtering.DepositVo;
import fi.re.firebackend.dto.recommendation.filtering.FundVo;
import fi.re.firebackend.jwt.JwtTokenProvider;
import fi.re.firebackend.service.recommendation.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/recommend")
public class RecommendationController {

    private final RecommendationService recmdService;
    private final JwtTokenProvider jwtTokenProvider;

    public RecommendationController(RecommendationService recmdService, JwtTokenProvider jwtTokenProvider) {
        this.recmdService = recmdService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/deposit")
    public ResponseEntity<List<DepositVo>> getDepositRecmd(HttpServletRequest request) {
        // 헤더에서 토큰 가져오기 (Bearer 접두어 제거 및 공백 제거)
        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return null;
        }

        //jwttokenprovider에서 받은 토큰으로 유저네임 가져오기
        String username = jwtTokenProvider.getUserInfo(token);
        List<DepositVo> recommendedDeposits = recmdService.getRecmdedDeposits(username);
        return new ResponseEntity<>(recommendedDeposits, HttpStatus.OK);
    }


    // 펀드 상품 추천 URL
    // 필터링 되어 선정된 몇 개의 펀드를 담아 반환
    @PostMapping("/fund")
    public ResponseEntity<List<FundVo>> getFundRecmd(HttpServletRequest request) {
        // 헤더에서 토큰 가져오기 (Bearer 접두어 제거 및 공백 제거)
        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return null;
        }
        //jwttokenprovider에서 받은 토큰으로 유저네임 가져오기
        String username = jwtTokenProvider.getUserInfo(token);
        List<FundVo> recommendedFunds = recmdService.getRecmdedFunds(username);
        return new ResponseEntity<>(recommendedFunds, HttpStatus.OK);
    }
}
