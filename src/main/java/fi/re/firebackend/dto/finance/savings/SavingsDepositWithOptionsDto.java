package fi.re.firebackend.dto.finance.savings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SavingsDepositWithOptionsDto {
    private SavingsDepositDto product;
    private List<OptionalDto> options;
}
