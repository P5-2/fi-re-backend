package fi.re.firebackend.service.recommendation;

import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.recommendation.MemberResponseDto;
import fi.re.firebackend.dto.recommendation.SavingsDepositsResponseDto;

import java.util.List;


public interface RecommendationService {
    // 적금 추천
    SavingsDepositsResponseDto getRecmdedDeposits(String username);

    // 예금 추천
    SavingsDepositsResponseDto getRecmdedSavings(String username);

    // 펀드 추천
    List<FundDto> getRecmdedFunds(String username);

    MemberResponseDto getMemberInfo(String username);
}
