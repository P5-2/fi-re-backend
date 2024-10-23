package fi.re.firebackend.dto.recommendation;

import fi.re.firebackend.dto.recommendation.vo.FilteredSavingsDepositsVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsDepositsResponseDto {
    private List<FilteredSavingsDepositsVo> savingsDeposits;
    private List<String> usedKeywords;
}