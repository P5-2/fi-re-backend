package fi.re.firebackend.service.recommendation.filters;


import fi.re.firebackend.dto.recommendation.MemberEntity;
import fi.re.firebackend.dto.recommendation.filtering.DepositVo;
import fi.re.firebackend.dto.recommendation.filtering.FundVo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContentsBasedFilterService {

    // 예적금 상품의 필터링
    public List<DepositVo> filterDeposits(final MemberEntity member, final List<DepositVo> depositsList) {
        member.parseKeywords();
        List<String> memberKeyword = member.getKeywordList();

        return depositsList.parallelStream()
                .filter(deposit -> {
                    if (deposit.getDepositEntity().getType().equals("예금")) {
                        // 예금 필터(자산이 minAmount~maxAmount 사이에 있는지 확인)
                        return deposit.getMinAmount().compareTo(BigDecimal.valueOf(member.getAssets())) <= 0 &&
                                deposit.getMaxAmount().compareTo(BigDecimal.valueOf(member.getAssets())) >= 0;
                    } else if (deposit.getDepositEntity().getType().equals("적금")) {
                        // 적금 필터(급여의 10~20%가 minAmount~maxAmount 사이에 있는지 확인)
                        BigDecimal salaryPercent = BigDecimal.valueOf(member.getSalary()).multiply(BigDecimal.valueOf(0.1));
                        BigDecimal salaryMaxPercent = BigDecimal.valueOf(member.getSalary()).multiply(BigDecimal.valueOf(0.2));
                        return deposit.getMinAmount().compareTo(salaryPercent) <= 0 &&
                                deposit.getMaxAmount().compareTo(salaryMaxPercent) >= 0;
                    }
                    return false;
                })
                .filter(deposit -> hasCommonKeyword(deposit.getDepositEntity().getKeywordList(), memberKeyword == null ? new ArrayList<>() : memberKeyword)) // 키워드 매칭
                .collect(Collectors.toList());
    }


    // 위험도에 따른 펀드 필터링
    public List<FundVo> filterFund(final MemberEntity member, final List<FundVo> fundList) {
        // 위험도 범위에 따른 필터링
        return fundList.parallelStream()
                .filter(fund -> fund.getDngrGrade() >= convertRiskPointToGrade(member.getRiskPoint()))
                .collect(Collectors.toList());
    }


    // 해당하는 키워드가 있는지 매칭
    private boolean hasCommonKeyword(final List<String> productKeywords, final List<String> memberKeywords) {
        for (String memberKeyword : memberKeywords) {
            if (productKeywords.contains(memberKeyword)) {
                return true;
            }
        }
        return false;
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
