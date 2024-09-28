package fi.re.firebackend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberGoalDto { // 프론트로 보날 때 쓰는 dto
    private String finPrdtCd;             // 금융 상품 코드
    private String prdType;               // 상품 유형 (예금 또는 적금)
    private String startDate;             // 시작일
    private String endDate;               // 만기일
    private Double monthlyDeposit;        // 매월 적금액
    private Double totalDeposit;          // 현재까지 적립된 금액
    private Integer remainingDays;        // 만기까지 남은 일수
    private Double expectedInterest;      // 예상 이자 금액

}
