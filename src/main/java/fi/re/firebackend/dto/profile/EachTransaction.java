package fi.re.firebackend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EachTransaction {
    private String description;
    private BigDecimal totalAmount;
    private BigDecimal currentMonthDeposit;
}
