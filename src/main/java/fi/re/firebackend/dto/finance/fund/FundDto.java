package fi.re.firebackend.dto.finance.fund;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundDto {
    private int prdNo;          // 상품 번호
    private String pname;       // 상품 이름
    private String type;        // 상품 유형
    private double rate;        // 3개월 수익률
    private int dngrGrade;      // 위험 등급
    private String region;      // 지역
    private String bseDt;       // 기준 날짜
    private int selectCount;    // 선택 횟수
    private double nav;         // 기준가
    private double sixMRate;    // 6개월 수익률
    private double oneYRate;    // 1년 수익률
}

