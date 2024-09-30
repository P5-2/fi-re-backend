package fi.re.firebackend.service.recommendation.filters;

import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.recommendation.MemberEntity;
import fi.re.firebackend.dto.recommendation.vo.ProcessedSavingsDepositVo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ContentsBasedFilterService {

    private static final Logger log = Logger.getLogger(ContentsBasedFilterService.class);

    // 예적금 상품의 필터링
    public FilteredProductsResult filterProducts(final MemberEntity member, final List<ProcessedSavingsDepositVo> depositsList) {
        member.parseKeywords();
        List<String> memberKeywords = member.getKeywordList() != null ? member.getKeywordList() : new ArrayList<>();
        Set<String> usedKeywords = new HashSet<>();

        List<ProcessedSavingsDepositVo> filteredDeposits = depositsList.parallelStream()
                .filter(deposit -> {
                    boolean withinRange = isWithinAssetRange(member, deposit) || isWithinSalaryRange(member, deposit);
                    if (withinRange) {
                        // 키워드가 일치하는 경우 수집
                        usedKeywords.addAll(deposit.getKeywords());
                    }
                    return withinRange && hasCommonKeyword(deposit.getKeywords(), memberKeywords);
                })
                .collect(Collectors.toList());
        log.info("filterProducts with " + filteredDeposits);
        return new FilteredProductsResult(filteredDeposits, new ArrayList<>(usedKeywords));
    }

    // 자산 범위 체크
    private boolean isWithinAssetRange(MemberEntity member, ProcessedSavingsDepositVo deposit) {
        if (deposit.getIntrRateTypeNm().equals("예금")) {
            // 예금일 경우, 회원의 자산이 maxLimit보다 크거나 같을 때 true
            return deposit.getMaxLimit() > 0 && member.getAssets() >= deposit.getMaxLimit();
        } else if (deposit.getIntrRateTypeNm().equals("적금")) {
            return true; // 적금일 경우 항상 true
        }
        return false;
    }


    // 급여 범위 체크(급여의 10~30% 수준인지)
    private boolean isWithinSalaryRange(MemberEntity member, ProcessedSavingsDepositVo deposit) {
        if (deposit.getIntrRateTypeNm().equals("적금") && (Double) deposit.getMaxLimit() != null) { // 적금일 경우
            double salaryPercent = member.getAssets() * 0.4;
            return deposit.getMaxLimit() >= salaryPercent;
        }
        return true;
    }

    // 위험도에 따른 펀드 필터링
    public List<FundDto> filterFund(final MemberEntity member, final List<FundDto> fundList) {
        return fundList.stream()
                .filter(fund -> fund.getDngrGrade() >= convertRiskPointToGrade(member.getRiskPoint()))
                .collect(Collectors.toList());
    }

    // 해당하는 키워드가 있는지 매칭
    private boolean hasCommonKeyword(final List<String> productKeywords, final List<String> memberKeywords) {
        return memberKeywords.stream().anyMatch(productKeywords::contains);
    }

    // 위험도 변환
    private int convertRiskPointToGrade(final int riskPoint) {
        if (riskPoint >= 34) {
            return 1; // 매우 높은 위험 등급 펀드
        } else if (riskPoint >= 28) {
            return 2; // 높은 위험 등급 펀드
        } else if (riskPoint >= 22) {
            return 3; // 중간 위험 등급 펀드
        } else if (riskPoint >= 16) {
            return 4; // 낮은 위험 등급 펀드
        } else {
            return 5; // 매우 낮은 위험 등급 펀드
        }
    }

    // 필터링된 결과를 반환할 클래스
    public static class FilteredProductsResult {
        private List<ProcessedSavingsDepositVo> deposits;
        private List<String> usedKeywords;

        public FilteredProductsResult(List<ProcessedSavingsDepositVo> deposits, List<String> usedKeywords) {
            this.deposits = deposits;
            this.usedKeywords = usedKeywords;
        }

        public List<ProcessedSavingsDepositVo> getDeposits() {
            return deposits;
        }

        public List<String> getUsedKeywords() {
            return usedKeywords;
        }
    }
}
