package fi.re.firebackend.dto.finance.savings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingOptionDto {
    private String saveTrm;
    private String intrRate;
    private String intrRate2;
}
