package fi.re.firebackend.service.recommendation.filters;


import fi.re.firebackend.dto.recommendation.filtering.DepositVo;
import fi.re.firebackend.dto.recommendation.FundEntity;
import fi.re.firebackend.dto.recommendation.MemberEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DemographicFilterService {

    // Filter Deposits based on Member information
    public List<DepositVo> filterDeposits(MemberEntity member, List<DepositVo> depositsList) {
        member.parseKeywords();
        return depositsList.stream()
                .filter(deposit -> deposit.getMinAmount().compareTo(BigDecimal.valueOf(member.getAssets())) <= 0) // Assets filter
                .filter(deposit -> deposit.getMaxAmount().compareTo(BigDecimal.valueOf(member.getSalary())) >= 0) // Salary fit for deposit
                .filter(deposit -> isAgeAppropriate(member.getAge())) // Age-based filtering
                .filter(deposit -> hasCommonKeyword(deposit.getDepositEntity().getKeywordList(), member.getKeywordList())) // Keyword matching
                .collect(Collectors.toList());
    }

    // Filter Funds based on Member information
    public List<FundEntity> filterFund(MemberEntity member, List<FundEntity> fundList) {
        return fundList.stream()
                .filter(fund -> fund.getDngrGrade() <= member.getRiskPoint()) // Risk tolerance match
                .filter(fund -> fund.getRate().compareTo(BigDecimal.valueOf(3.0)) >= 0) // Minimum rate filter
//                .filter(fund -> hasCommonKeyword(fund.getKeyword(), member.getKeyword())) // Keyword matching
                .collect(Collectors.toList());
    }

    // Helper method to check if the member's age meets product criteria
    private boolean isAgeAppropriate(int age) {
        // Example: Only recommend if age is greater than or equal to 18
        return age >= 18;
    }

    // Helper method for checking keyword matching between product and member
    private boolean hasCommonKeyword(List<String> productKeywords, List<String> memberKeywords) {
        for (String memberKeyword : memberKeywords) {
            if (productKeywords.contains(memberKeyword)) {
                return true; // Return true if there is any common keyword
            }
        }
        return false; // No common keyword found
    }
}

