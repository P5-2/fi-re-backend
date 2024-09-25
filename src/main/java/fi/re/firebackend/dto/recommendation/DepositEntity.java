package fi.re.firebackend.dto.recommendation;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DepositEntity {
    private int prdNo;          // 상품 번호
    private String pname;       // 상품명
    private String type;        // 상품 유형 (정기예금 등)
    private String bname;       // 은행명
    private BigDecimal minRate; // 최소 이자율
    private BigDecimal maxRate; // 최대 이자율
    private int subPeriod;      // 가입 기간 (개월)
    private String subAmount;   // 최소 가입금액
    private String target;      // 상품 목표
    private String benefit;     // 혜택
    private String description; // 상품 설명
    private int selectCount;    // 선택 횟수
    //    private List<String> keyword;  // 관련 키워드
    private String keyword;     // 관련 키워드
    private List<String> keywordList; // 분리된 키워드 리스트
}
