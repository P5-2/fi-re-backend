package fi.re.firebackend.dto.finance.savings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsDto {
    private int prdNo;
    private String pname;
    private String type;
    private String bname;
    private double minRate;
    private double maxRate;
    private int subPeriod;
    private String subAmount;
    private String target;
    private String benefit;
    private String description;
    private int selectCount;
    private String keyword;
    private boolean InCart;
}
