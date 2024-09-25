package fi.re.firebackend.dto.finance.fund;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundDto {
    private int prdNo;
    private String pname;
    private String type;
    private double rate;
    private int dngrGrade;
    private String region;
    private String bseDt;
    private int selectCount;
    private double nav;
}
