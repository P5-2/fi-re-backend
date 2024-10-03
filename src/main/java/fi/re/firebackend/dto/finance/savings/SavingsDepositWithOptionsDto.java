package fi.re.firebackend.dto.finance.savings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsDepositWithOptionsDto {
    private SavingsDepositDto savingsDeposit;
    private List<OptionalDto> options;
}
