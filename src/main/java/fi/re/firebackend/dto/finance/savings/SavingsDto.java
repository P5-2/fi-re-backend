package fi.re.firebackend.dto.finance.savings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsDto {
    private String finPrdtCd;
    private String korCoNm;
    private String finPrdtNm;
    private String joinWay;
    private String mtrtInt;
    private String spclCnd;
    private String joinDeny;
    private String joinMember;
    private String etcNote;
    private long maxLimit;
    private List<SavingOptionDto> options;

//    private int prdNo;
//    private String pname;
//    private String type;
//    private String bname;
//    private double minRate;
//    private double maxRate;
//    private int subPeriod;
//    private String subAmount;
//    private String target;
//    private String benefit;
//    private String description;
//    private int selectCount;
//    private String keyword;
}

