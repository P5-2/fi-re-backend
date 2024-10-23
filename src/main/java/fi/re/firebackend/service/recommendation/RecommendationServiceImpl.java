package fi.re.firebackend.service.recommendation;

import fi.re.firebackend.dao.finance.fund.FundDao;
import fi.re.firebackend.dao.finance.savings.SavingsDepositDao;
import fi.re.firebackend.dao.member.MemberDaotwo;
import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.OptionalDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositWithOptionsDto;
import fi.re.firebackend.dto.member.MemberDto;
import fi.re.firebackend.dto.recommendation.MemberEntity;
import fi.re.firebackend.dto.recommendation.MemberResponseDto;
import fi.re.firebackend.dto.recommendation.SavingsDepositEntity;
import fi.re.firebackend.dto.recommendation.SavingsDepositsResponseDto;
import fi.re.firebackend.dto.recommendation.vo.FilteredSavingsDepositsVo;
import fi.re.firebackend.dto.recommendation.vo.ProcessedSavingsDepositVo;
import fi.re.firebackend.service.recommendation.filters.ContentsBasedFilterService;
import fi.re.firebackend.service.recommendation.filters.ItemBasedFilterService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger log = Logger.getLogger(RecommendationServiceImpl.class);
    private final ContentsBasedFilterService contentsBasedFilterService;
    private final ItemBasedFilterService itemBasedFilterService;
    private final FundDao fundDao; // 펀드 레포지토리
    private final MemberDaotwo memberDao;
    private final SavingsDepositDao savingsDepositDao;

    // MemberDto를 MemberEntity로 변환
    public static MemberEntity convertToMemberEntity(MemberDto memberDto) {
        if (memberDto == null) {
            return null;
        }

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername(memberDto.getUsername());
        memberEntity.setAge(Integer.parseInt(memberDto.getAge()));
        memberEntity.setSalary(memberDto.getSalary());
        memberEntity.setAssets(memberDto.getAssets());
        memberEntity.setRiskPoint(memberDto.getRiskPoint());
        memberEntity.setGoalAmount(memberDto.getGoalAmount());
        memberEntity.setKeyword(memberDto.getKeyword());

        memberEntity.parseKeywords();

        return memberEntity;
    }

    //예금 추천
    @Override
    public SavingsDepositsResponseDto getRecmdedDeposits(String username) {
        // 1. username으로 유저 정보 가져오기
        MemberEntity user = convertToMemberEntity(memberDao.findMember(username));
        log.info("user: " + user);

        // 2. DB에서 모든 적금 조회
        // 모든 적금 데이터를 가져오고 SavingsDepositEntity 리스트로 변환
        List<SavingsDepositEntity> dividedAlldeposits = getAllSavingsDeposits(400, "D");

        // 3. SavingsDepositEntity를 ProcessedSavingsDepositVo로 변환
        List<ProcessedSavingsDepositVo> depositVos = dividedAlldeposits.stream().map(ProcessedSavingsDepositVo::new).collect(Collectors.toList());

        // 4. 컨텐츠 기반 필터링
        ContentsBasedFilterService.FilteredProductsResult result = contentsBasedFilterService.filterProducts(user, depositVos);

        List<ProcessedSavingsDepositVo> filteredDeposits = result.getDeposits();
        List<String> usedKeywords = result.getUsedKeywords();

        // 5. 기준으로 삼을 top3 적금 상품 가져오기
        List<SavingsDepositEntity> dividedHotDeposits = getAllSavingsDeposits(20, "D");
        List<ProcessedSavingsDepositVo> hotDepositVos = dividedHotDeposits.stream().map(ProcessedSavingsDepositVo::new).collect(Collectors.toList());

        // 6. 아이템 기반 추천
        List<ProcessedSavingsDepositVo> recommendedDeposits = itemBasedFilterService.filteringSavingsDeposits(hotDepositVos, filteredDeposits);

        // 보내기 전에 dto로 옮겨서 보내기
        List<FilteredSavingsDepositsVo> resultDto = convertToFilteredVo(recommendedDeposits).stream().limit(10).collect(Collectors.toList());

        return new SavingsDepositsResponseDto(resultDto, usedKeywords);
    }

    //적금추천
    @Override
    public SavingsDepositsResponseDto getRecmdedSavings(String username) {
        // 1. username으로 유저 정보 가져오기
        MemberEntity user = convertToMemberEntity(memberDao.findMember(username));
        log.info("user: " + user);

        // 2. DB에서 모든 예금 조회
        List<SavingsDepositEntity> allSavings = getAllSavingsDeposits(400, "S");

        // 3. DepositEntity를 ProcessedSavingsDepositVo 변환
        List<ProcessedSavingsDepositVo> savingsVos = allSavings.parallelStream().map(ProcessedSavingsDepositVo::new).collect(Collectors.toList());

        // 4. 컨텐츠 기반 필터링
        ContentsBasedFilterService.FilteredProductsResult result = contentsBasedFilterService.filterProducts(user, savingsVos);

        List<ProcessedSavingsDepositVo> filteredSavings = result.getDeposits();
        List<String> usedKeywords = result.getUsedKeywords();

        // 5. 기준으로 삼을 예금 상품 가져오기
        List<SavingsDepositEntity> hotSavings = getAllSavingsDeposits(20, "S");

        List<ProcessedSavingsDepositVo> hotSavingVos = hotSavings.stream().map(ProcessedSavingsDepositVo::new).collect(Collectors.toList());

        // 6. 아이템 기반 추천
        List<ProcessedSavingsDepositVo> recommendedSavings = itemBasedFilterService.filteringSavingsDeposits(hotSavingVos, filteredSavings);

        // 보내기 전에 dto로 옮겨서 보내기
        List<FilteredSavingsDepositsVo> resultDto = convertToFilteredVo(recommendedSavings).stream().limit(10).collect(Collectors.toList());
        return new SavingsDepositsResponseDto(resultDto, usedKeywords);
    }

    @Override
    public List<FundDto> getRecmdedFunds(String username) {
        // 1. username으로 유저 정보 가져오기
        MemberEntity user = convertToMemberEntity(memberDao.findMember(username));

        // 2. DB에서 모든 펀드 조회
        List<FundDto> allFunds = fundDao.all();

        // 3. 컨텐츠 기반 필터링
        List<FundDto> filteredFunds = contentsBasedFilterService.filterFund(user, allFunds);

        // 4.기준 펀드 가져오기
        List<FundDto> hotFunds = fundDao.hot();

        // 5. 아이템 기반 추천
        List<FundDto> recommendedFunds = itemBasedFilterService.filteredFunds(hotFunds, filteredFunds).stream().distinct().sorted(Comparator.comparingDouble(FundDto::getOneYRate).reversed()) // oneYRate 수익률 높은 순으로 정렬
                .limit(10).collect(Collectors.toList());

        return recommendedFunds;
    }

    @Override
    public MemberResponseDto getMemberInfo(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 사용자 이름입니다.");
        }

        MemberDto user = memberDao.findMember(username);
        if (user == null) {
            return null;
        }

        return new MemberResponseDto().builder().nickname(user.getNickname()).age(user.getAge()).salary(user.getSalary()).assets(user.getAssets()).riskPoint(user.getRiskPoint()).exp(user.getExp()).goalAmount(user.getGoalAmount()).keyword(user.getKeyword()).build();
    }

    private List<SavingsDepositEntity> getAllSavingsDeposits(int limit, String prdtDiv) {
        List<SavingsDepositWithOptionsDto> combinedDeposits = savingsDepositDao.getAllProducts(limit, prdtDiv);
        List<SavingsDepositEntity> allDeposits = new ArrayList<>();

        for (SavingsDepositWithOptionsDto dto : combinedDeposits) {
            SavingsDepositDto savingsDeposit = dto.getSavingsDeposit();
            List<OptionalDto> options = dto.getOptions();

            // 각 옵션의 인덱스와 함께 SavingsDepositEntity를 생성
            for (int j = 0; j < options.size(); j++) {
                OptionalDto option = options.get(j);
                SavingsDepositEntity depositEntity = new SavingsDepositEntity();

                // SavingsDeposit 정보를 depositEntity에 설정
                depositEntity.setFinPrdtCd(savingsDeposit.getFinPrdtCd());
                depositEntity.setKorCoNm(savingsDeposit.getKorCoNm());
                depositEntity.setFinPrdtNm(savingsDeposit.getFinPrdtNm());
                depositEntity.setJoinWay(savingsDeposit.getJoinWay());
                depositEntity.setSpclCnd(savingsDeposit.getSpclCnd());
                depositEntity.setJoinMember(savingsDeposit.getJoinMember());
                depositEntity.setEtcNote(savingsDeposit.getEtcNote());
                depositEntity.setMaxLimit(savingsDeposit.getMaxLimit() != null ? savingsDeposit.getMaxLimit() : 0L);

                // 옵션 정보를 depositEntity에 설정 (인덱스 맞추기)
                depositEntity.setIntrRateTypeNm(option.getIntrRateTypeNm());
                depositEntity.setSaveTrm(option.getSaveTrm());
                depositEntity.setIntrRate(option.getIntrRate());
                depositEntity.setIntrRate2(option.getIntrRate2());

                // 기본 선택 카운트는 0으로 설정
                depositEntity.setSelectCount(0);

                // 생성된 SavingsDepositEntity를 리스트에 추가
                allDeposits.add(depositEntity);
            }
        }

        return allDeposits;
    }

    // ProcessedSavingsDepositVo 리스트를 FilteredSavingsDepositsVo 리스트로 변환
    public List<FilteredSavingsDepositsVo> convertToFilteredVo(List<ProcessedSavingsDepositVo> processedList) {
        return processedList.stream().distinct().sorted(Comparator.comparingDouble(ProcessedSavingsDepositVo::getIntrRate2).reversed()).map(this::convertToFilteredVo).collect(Collectors.toList());
    }

    // 개별 ProcessedSavingsDepositVo를 FilteredSavingsDepositsVo로 변환
    private FilteredSavingsDepositsVo convertToFilteredVo(ProcessedSavingsDepositVo processedVo) {
        FilteredSavingsDepositsVo filteredVo = new FilteredSavingsDepositsVo();

        filteredVo.setFinPrdtCd(processedVo.getFinPrdtCd());
        filteredVo.setKorCoNm(processedVo.getKorCoNm());
        filteredVo.setFinPrdtNm(processedVo.getFinPrdtNm());
        filteredVo.setJoinWay(processedVo.getJoinWay());
        filteredVo.setKeywords(processedVo.getKeywords());
        filteredVo.setMaxLimit(BigDecimal.valueOf(processedVo.getMaxLimit())); // double -> BigDecimal
        filteredVo.setIntrRateTypeNm(processedVo.getIntrRateTypeNm());
        filteredVo.setSaveTrm(processedVo.getSaveTrm());
        filteredVo.setIntrRate(BigDecimal.valueOf(processedVo.getIntrRate()));
        filteredVo.setIntrRate2(BigDecimal.valueOf(processedVo.getIntrRate2()));
        filteredVo.setSelectCount(processedVo.getSelectCount());

        return filteredVo;
    }
}

