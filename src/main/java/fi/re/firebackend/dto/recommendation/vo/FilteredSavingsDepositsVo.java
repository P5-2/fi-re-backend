package fi.re.firebackend.dto.recommendation.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class FilteredSavingsDepositsVo {
    private String finPrdtCd; // 금융상품코드
    private String korCoNm; // 은행명
    private String finPrdtNm; // 금융상품이름
    private String joinWay; // 가입 방법
    private List<String> keywords; // 추출된 키워드 리스트
    private BigDecimal maxLimit; // 최대 한도
    private String intrRateTypeNm; // 저축 금리 유형명(단리 or 복리)
    private int saveTrm; // 저축 기간 (숫자로 변환)
    private BigDecimal intrRate; // min
    private BigDecimal intrRate2; // max
    private int selectCount; // 선택 횟수
}
