package fi.re.firebackend.controller.recommendation;

import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.recommendation.MemberResponseDto;
import fi.re.firebackend.dto.recommendation.SavingsDepositsResponseDto;
import fi.re.firebackend.jwt.JwtTokenProvider;
import fi.re.firebackend.service.recommendation.RecommendationService;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/recommend")
public class RecommendationController {

    private static final Logger log = Logger.getLogger(RecommendationController.class);
    private final RecommendationService recmdService;
    private final JwtTokenProvider jwtTokenProvider;

    public RecommendationController(RecommendationService recmdService, JwtTokenProvider jwtTokenProvider) {
        this.recmdService = recmdService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/deposit")
    public ResponseEntity<SavingsDepositsResponseDto> getDepositRecmd(HttpServletRequest request) {
        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return null;
        }

        String username = jwtTokenProvider.getUserInfo(token);
        SavingsDepositsResponseDto recommendedDeposits = recmdService.getRecmdedDeposits(username);
        log.info("recommendedDeposits : " + recommendedDeposits.toString());
        return new ResponseEntity<>(recommendedDeposits, HttpStatus.OK);
    }

    @GetMapping("/savings")
    public ResponseEntity<SavingsDepositsResponseDto> getSavingsRecmd(HttpServletRequest request) {
        // 헤더에서 토큰 가져오기 (Bearer 접두어 제거 및 공백 제거)
        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return null;
        }

        String username = jwtTokenProvider.getUserInfo(token);
        SavingsDepositsResponseDto recommendedSavings = recmdService.getRecmdedSavings(username);
        log.info("recommendedSavings : " + recommendedSavings.toString());
        return new ResponseEntity<>(recommendedSavings, HttpStatus.OK);
    }


    // 펀드 상품 추천 URL
    // 필터링 되어 선정된 몇 개의 펀드를 담아 반환
    @GetMapping("/fund")
    public ResponseEntity<List<FundDto>> getFundRecmd(HttpServletRequest request) {
        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return null;
        }

        String username = jwtTokenProvider.getUserInfo(token);
        List<FundDto> recommendedFunds = recmdService.getRecmdedFunds(username);
        return new ResponseEntity<>(recommendedFunds, HttpStatus.OK);
    }


    //추천 서비스를 이용하는 유저 정보
    @GetMapping("/member")
    public ResponseEntity<MemberResponseDto> getMemberInfo(HttpServletRequest request) {
        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return null;
        }

        String username = jwtTokenProvider.getUserInfo(token);
        MemberResponseDto memberInfo = recmdService.getMemberInfo(username);
        return new ResponseEntity<>(memberInfo, HttpStatus.OK);
    }
}
