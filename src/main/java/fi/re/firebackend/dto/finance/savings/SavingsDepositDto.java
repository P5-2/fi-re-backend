package fi.re.firebackend.dto.finance.savings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsDepositDto {

    private String finPrdtCd; //Financial Product Code
    private String korCoNm; //bankName
    private String finPrdtNm; //Financial Product Name
    private String joinWay; // 가입방법
    private String spclCnd; //우대조건
    private String joinMember; //가입대상
    private String etcNote; //기타 유의사항
    private long maxLimit; //최대한도
//  private List<SavingsDepositOptionDto> options; //적금 옵션 리스트
    private String intrRateTypeNm; //저축 금리 유형명(단리 or 복리)
    private String saveTrm; //저축 기간
    private String intrRate; //minRate 기본금리
    private String intrRate2; //maxRate 최고금리

}

