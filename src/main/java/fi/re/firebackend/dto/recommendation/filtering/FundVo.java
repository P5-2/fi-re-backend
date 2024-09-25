package fi.re.firebackend.dto.recommendation.filtering;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

//추천 필터링을 위한 펀드 VO
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FundVo {
    private int prdNo;          // 상품 번호
    private String pname;       // 상품명
    private String type;        // 상품 유형 (펀드 종류)
    private BigDecimal rate;    // 수익률
    private int dngrGrade;      // 위험 등급
    private String region;      // 투자 지역
}
