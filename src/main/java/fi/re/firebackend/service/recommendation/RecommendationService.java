package fi.re.firebackend.service.recommendation;

import fi.re.firebackend.dto.recommendation.DepositEntity;
import fi.re.firebackend.dto.recommendation.FundEntity;
import fi.re.firebackend.dto.recommendation.MemberEntity;
import fi.re.firebackend.dto.recommendation.filtering.DepositVo;

import java.util.List;


public interface RecommendationService {
    // 예적금 추천
    List<DepositVo> getRecmdedDeposits(String username);

    // 펀드 추천
    List<FundEntity> getRecmdedFunds(MemberEntity member);
}
