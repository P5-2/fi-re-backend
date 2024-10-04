package fi.re.firebackend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberSavingsResponseDto { // 서버에서 응답해주는 내용
    private String goalName; // 목표 이름
    private String finPrdtCd; // 상품 코드(prdNo)
    private String finPrdtNm; // 상품 이름
    private String intrRateTypeNm; // 단리 복리
    private Date startDate; // 가입일
    private Date endDate; // 가입일
    private double minRate;
    private double maxRate;
    private int savedAmount; // 지금까지 저금한 금액
    private int monthlyDeposit; // 이번 달 입금 금액
    private String bankname;
    private int targetAmount; //목표 금액
}
