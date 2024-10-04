package fi.re.firebackend.dto.recommendation;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingsDepositEntity {

    private String finPrdtCd; //Financial Product Code
    private String korCoNm; //bankName
    private String finPrdtNm; //Financial Product Name
    private String joinWay; // 가입방법
    private String spclCnd; //우대조건
    private String joinMember; //가입대상
    private String etcNote; //기타 유의사항
    private long maxLimit; //최대한도
    private String intrRateTypeNm; //저축 금리 유형명(단리 or 복리)
    private String saveTrm; //저축 기간
    private String intrRate; //minRate 기본금리
    private String intrRate2; //maxRate 최고금리
    private int selectCount; //상품 선택 횟수
}

