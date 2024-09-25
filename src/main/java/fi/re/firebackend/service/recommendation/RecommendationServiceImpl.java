package fi.re.firebackend.service.recommendation;

import fi.re.firebackend.dao.recommendation.DepositDao;
import fi.re.firebackend.dao.recommendation.FundRcmdDao;
import fi.re.firebackend.dto.recommendation.DepositEntity;
import fi.re.firebackend.dto.recommendation.MemberEntity;
import fi.re.firebackend.dto.recommendation.filtering.DepositVo;
import fi.re.firebackend.dto.recommendation.filtering.FundVo;
import fi.re.firebackend.service.recommendation.filters.ContentsBasedFilterService;
import fi.re.firebackend.service.recommendation.filters.ItemBasedFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final ContentsBasedFilterService contentsBasedFilterService;
    private final ItemBasedFilterService itemBasedFilterService;
    private final DepositDao depositDao; // 예적금 레포지토리
    private final FundRcmdDao fundRcmdDao; // 펀드 레포지토리

    private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);
    @Autowired
    public RecommendationServiceImpl(ContentsBasedFilterService contentsBasedFilterService,
                                     ItemBasedFilterService itemBasedFilterService,
                                     DepositDao depositDao,
                                     FundRcmdDao fundRcmdDao) {
        this.contentsBasedFilterService = contentsBasedFilterService;
        this.itemBasedFilterService = itemBasedFilterService;
        this.depositDao = depositDao;
        this.fundRcmdDao = fundRcmdDao;
    }

    @Override
    public List<DepositVo> getRecmdedDeposits(String username) {
        // 1. username으로 유저 정보 가져오기
        MemberEntity user = depositDao.getMemberInfo(username);
        System.out.println("user: " + user);

        // 2. DB에서 모든 예적금 조회
        List<DepositEntity> allSavings = depositDao.getAllDeposit();

        // 3. DepositEntity를 DepositVo로 변환
        List<DepositVo> depositVos = allSavings.stream()
                .map(DepositVo::new)
                .collect(Collectors.toList());

        // 4. 컨텐츠 기반 필터링
        List<DepositVo> filteredSavings = contentsBasedFilterService.filterDeposits(user, depositVos);

        // 5. 아이템 기반 추천
        List<DepositVo> recommendedDeposits = itemBasedFilterService.recmdDeposits(filteredSavings, filteredSavings);

        return recommendedDeposits.stream().limit(5).collect(Collectors.toList());
    }

    @Override
    public List<FundVo> getRecmdedFunds(String username) {
        // 1. username으로 유저 정보 가져오기
        MemberEntity user = depositDao.getMemberInfo(username);

        // 2. DB에서 모든 펀드 조회
        List<FundVo> allFunds = fundRcmdDao.getAllFund();

        // 3. 컨텐츠 기반 필터링
        List<FundVo> filteredFunds = contentsBasedFilterService.filterFund(user, allFunds);

        // 4. 아이템 기반 추천
        List<FundVo> recommendedFunds = itemBasedFilterService.recmdFunds(filteredFunds, filteredFunds);

        return recommendedFunds.stream().limit(5).collect(Collectors.toList());
    }

}

