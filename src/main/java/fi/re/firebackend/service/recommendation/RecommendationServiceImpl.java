package fi.re.firebackend.service.recommendation;

import fi.re.firebackend.dao.finance.fund.FundDao;
import fi.re.firebackend.dao.member.MemberDaotwo;
import fi.re.firebackend.dao.recommendation.DepositRcmdDao;
import fi.re.firebackend.dao.recommendation.SavingsRcmdDao;
import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.member.MemberDto;
import fi.re.firebackend.dto.recommendation.MemberEntity;
import fi.re.firebackend.dto.recommendation.MemberResponseDto;
import fi.re.firebackend.dto.recommendation.SavingsDepositEntity;
import fi.re.firebackend.dto.recommendation.SavingsDepositsDto;
import fi.re.firebackend.dto.recommendation.vo.ProcessedSavingsDepositVo;
import fi.re.firebackend.service.recommendation.filters.ContentsBasedFilterService;
import fi.re.firebackend.service.recommendation.filters.ItemBasedFilterService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger log = Logger.getLogger(RecommendationServiceImpl.class);
    private final ContentsBasedFilterService contentsBasedFilterService;
    private final ItemBasedFilterService itemBasedFilterService;
    private final DepositRcmdDao depositDao; //적금 레포
    private final SavingsRcmdDao savingsDao; //예금 레포
    private final FundDao fundDao; // 펀드 레포지토리
    private final MemberDaotwo memberDao;

    //적금 추천
    @Override
    public List<SavingsDepositsDto> getRecmdedDeposits(String username) {
        // 1. username으로 유저 정보 가져오기
        MemberEntity user = convertToEntity(memberDao.findMember(username));
        log.info("user: " + user);

        // 2. DB에서 모든 적금 조회
        List<SavingsDepositEntity> allDeposits = depositDao.getAllDeposit();

        // 3. SavingsDepositEntity를 ProcessedSavingsDepositVo로 변환
        List<ProcessedSavingsDepositVo> depositVos = allDeposits.stream()
                .map(ProcessedSavingsDepositVo::new)
                .collect(Collectors.toList());

        // 4. 컨텐츠 기반 필터링
        List<ProcessedSavingsDepositVo> filteredSavings = contentsBasedFilterService.filterProducts(user, depositVos);
//        log.info("Filtered Savings: {}", (Throwable) filteredSavings); // 로깅 메시지 개선

        // 5. 기준으로 삼을 top3 적금 상품 가져오기
        List<ProcessedSavingsDepositVo> hotDeposits = depositDao.getHotDeposit().stream()
                .map(ProcessedSavingsDepositVo::new)
                .collect(Collectors.toList());

        // 6. 아이템 기반 추천
        List<ProcessedSavingsDepositVo> recommendedDeposits = itemBasedFilterService.filteringSavingsDeposits(hotDeposits, filteredSavings);
//        log.info("Recommended Deposits: {}", (Throwable) recommendedDeposits); // 로깅 메시지 개선

        // 보내기 전에 dto로 옮겨서 보내기
        List<SavingsDepositsDto> resultDto = recommendedDeposits.stream()
                .map(vo -> new SavingsDepositsDto())
                .collect(Collectors.toList());


        return resultDto;
    }

    //예금추천
    @Override
    public List<SavingsDepositsDto> getRecmdedSavings(String username) {
        // 1. username으로 유저 정보 가져오기
        MemberEntity user = convertToEntity(memberDao.findMember(username));
        log.info("user: " + user);

        // 2. DB에서 모든 예금 조회
        List<SavingsDepositEntity> allSavings = depositDao.getAllDeposit(); //여기 가져오는 부분 변경하기 1. 예적금 분리되어있으므로 예금, 적금 따로 추천하도록 분리

        // 3. DepositEntity를 ProcessedSavingsDepositVo 변환
        List<ProcessedSavingsDepositVo> depositVos = allSavings.parallelStream()
                .map(ProcessedSavingsDepositVo::new)
                .collect(Collectors.toList());

        // 4. 컨텐츠 기반 필터링
        List<ProcessedSavingsDepositVo> filteredSavings = contentsBasedFilterService.filterProducts(user, depositVos);
        log.info(filteredSavings);

        // 5. 기준으로 삼을 예금 상품 가져오기
        List<ProcessedSavingsDepositVo> hotSavings = savingsDao.getHotSavings().stream()
                .map(ProcessedSavingsDepositVo::new)
                .collect(Collectors.toList());

        // 6. 아이템 기반 추천
        List<ProcessedSavingsDepositVo> recommendedDeposits = itemBasedFilterService.filteringSavingsDeposits(hotSavings, filteredSavings);
        log.info(recommendedDeposits);

        // 보내기 전에 dto로 옮겨서 보내기
        List<SavingsDepositsDto> resultDto = recommendedDeposits.stream()
                .map(vo -> new SavingsDepositsDto())
                .collect(Collectors.toList());

        return resultDto;
    }

    @Override
    public List<FundDto> getRecmdedFunds(String username) {
        // 1. username으로 유저 정보 가져오기
        MemberEntity user = convertToEntity(memberDao.findMember(username));

        // 2. DB에서 모든 펀드 조회
        List<FundDto> allFunds = fundDao.all();

        // 3. 컨텐츠 기반 필터링
        List<FundDto> filteredFunds = contentsBasedFilterService.filterFund(user, allFunds);

        // 4.기준 펀드 가져오기
        List<FundDto> hotFunds = fundDao.hot();

        // 5. 아이템 기반 추천
        List<FundDto> recommendedFunds = itemBasedFilterService.filteredFunds(hotFunds, filteredFunds)
                .stream()
                .sorted(Comparator.comparingDouble(this::calculateAverageRate)) // 평균 수익률로 정렬
                .collect(Collectors.toList());

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

        return new MemberResponseDto().builder()
                .nickname(user.getNickname())
                .age(user.getAge())
                .salary(user.getSalary())
                .assets(user.getAssets())
                .riskPoint(user.getRiskPoint())
                .exp(user.getExp())
                .goalAmount(user.getGoalAmount())
                .keyword(user.getKeyword())
                .build();
    }


    // MemberDto를 MemberEntity로 변환하는 메서드
    public static MemberEntity convertToEntity(MemberDto memberDto) {
        if (memberDto == null) {
            return null; // null 체크
        }

        // MemberEntity 생성
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername(memberDto.getUsername());
        memberEntity.setAge(Integer.parseInt(memberDto.getAge())); // 나이를 int로 변환
        memberEntity.setSalary(memberDto.getSalary());
        memberEntity.setAssets(memberDto.getAssets());
        memberEntity.setRiskPoint(memberDto.getRiskPoint());
        memberEntity.setGoalAmount(memberDto.getGoalAmount());
        memberEntity.setKeyword(memberDto.getKeyword());

        // 키워드 파싱
        memberEntity.parseKeywords();

        return memberEntity;
    }

    // 펀드 평균 수익률 계산
    private double calculateAverageRate(FundDto fund) {
        double threeMonthRate = fund.getRate();
        double sixMonthRate = fund.getSixMRate();
        double oneYearRate = fund.getOneYRate();

        // 3개월, 6개월, 1년 수익률의 평균 계산
        return (threeMonthRate + sixMonthRate + oneYearRate) / 3.0;
    }
}

