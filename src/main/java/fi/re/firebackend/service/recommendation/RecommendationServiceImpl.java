package fi.re.firebackend.service.recommendation;

import fi.re.firebackend.dao.recommendation.DepositDao;
import fi.re.firebackend.dao.recommendation.FundRcmdDao;
import fi.re.firebackend.dto.recommendation.DepositEntity;
import fi.re.firebackend.dto.recommendation.FundEntity;
import fi.re.firebackend.dto.recommendation.MemberEntity;
import fi.re.firebackend.dto.recommendation.filtering.DepositVo;
import fi.re.firebackend.service.recommendation.filters.DemographicFilterService;
import fi.re.firebackend.service.recommendation.filters.ItemBasedFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final DemographicFilterService demographicFilterService;
    private final ItemBasedFilterService itemBasedFilterService;
    private final DepositDao depositDao; // 예적금 레포지토리
    private final FundRcmdDao fundRcmdDao; // 펀드 레포지토리

    @Autowired
    public RecommendationServiceImpl(DemographicFilterService demographicFilterService,
                                     ItemBasedFilterService itemBasedFilterService,
                                     DepositDao depositDao,
                                     FundRcmdDao fundRcmdDao) {
        this.demographicFilterService = demographicFilterService;
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
                .map(DepositVo::new) // DepositEntity를 DepositVo로 변환하는 생성자 필요
                .collect(Collectors.toList());

        // 4. 데모그래픽 필터링
        List<DepositVo> filteredSavings = demographicFilterService.filterDeposits(user, depositVos);

        // 5. 아이템 기반 추천
        List<DepositVo> recommendedDeposits = filteredSavings.stream()
                .map(savings -> itemBasedFilterService.recmdDeposits(savings, filteredSavings))
                .collect(Collectors.toList());

        return recommendedDeposits;
    }


    @Override
    public List<FundEntity> getRecmdedFunds(MemberEntity member) {
        // 1. DB에서 모든 펀드 조회
        List<FundEntity> allFunds = fundRcmdDao.getAllFund();

        // 2. 예적금의 정보를 필터링 하기 용이하도록 정리


        // 3. 데모그래픽 필터링
        List<FundEntity> filteredFunds = demographicFilterService.filterFund(member, allFunds);

        // 4. 아이템 기반 추천
        List<FundEntity> recommendedFunds = filteredFunds.stream()
                .map(fund -> itemBasedFilterService.recmdFund(fund, filteredFunds))
                .collect(Collectors.toList());

        return recommendedFunds;
    }
}

