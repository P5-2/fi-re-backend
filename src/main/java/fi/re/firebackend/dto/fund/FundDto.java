package fi.re.firebackend.dto.fund;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundDto {
    private int prdNo;
    private String pname;
    private String type;
    private BigDecimal rate;
    private int dngrGrade;
    private String region;
    private String bseDt;
    private int selectCount;
}
