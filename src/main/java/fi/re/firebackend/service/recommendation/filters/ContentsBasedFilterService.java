package fi.re.firebackend.service.recommendation.filters;

import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.recommendation.MemberEntity;
import fi.re.firebackend.dto.recommendation.vo.ProcessedSavingsDepositVo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContentsBasedFilterService {

    // 예적금 상품의 필터링
    public List<ProcessedSavingsDepositVo> filterProducts(final MemberEntity member, final List<ProcessedSavingsDepositVo> depositsList) {
        member.parseKeywords();
        List<String> memberKeywords = member.getKeywordList() != null ? member.getKeywordList() : new ArrayList<>();

        return depositsList.parallelStream()
                .filter(deposit -> isWithinAssetRange(member, deposit) || isWithinSalaryRange(member, deposit))
                .filter(deposit -> hasCommonKeyword(deposit.getKeywords(), memberKeywords))
                .collect(Collectors.toList());
    }

    // 자산 범위 체크
    private boolean isWithinAssetRange(MemberEntity member, ProcessedSavingsDepositVo deposit) {
        return deposit.getMaxLimit() > 0 && deposit.getMaxLimit() >= member.getAssets();
    }

    // 급여 범위 체크(급여의 10~30% 수준 인지)
    private boolean isWithinSalaryRange(MemberEntity member, ProcessedSavingsDepositVo deposit) {
        if (deposit.getMaxLimit() > 0) { // 최대 한도가 있는 경우
            double salaryPercent = member.getSalary() * 0.1;
            double salaryMaxPercent = member.getSalary() * 0.3;
            return deposit.getMaxLimit() >= salaryPercent && deposit.getMaxLimit() <= salaryMaxPercent;
        }
        return false;
    }

    // 위험도에 따른 펀드 필터링
    public List<FundDto> filterFund(final MemberEntity member, final List<FundDto> fundList) {
        return fundList.parallelStream()
                .filter(fund -> fund.getDngrGrade() >= convertRiskPointToGrade(member.getRiskPoint()))
                .collect(Collectors.toList());
    }

    // 해당하는 키워드가 있는지 매칭
    private boolean hasCommonKeyword(final List<String> productKeywords, final List<String> memberKeywords) {
        return memberKeywords.parallelStream().anyMatch(productKeywords::contains);
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
}
