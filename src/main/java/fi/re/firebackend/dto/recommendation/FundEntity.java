package fi.re.firebackend.dto.recommendation;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FundEntity {
    private int prdNo;          // 상품 번호
    private String pname;       // 상품명
    private String type;        // 상품 유형 (펀드 종류)
    private BigDecimal rate;    // 수익률
    private int dngrGrade;      // 위험 등급
    private String region;      // 투자 지역
    private Timestamp bseDt;    // 기준일자
    private int selectCount;    // 선택 횟수
}
// 사용 할 지 안 할 지 고민해보기