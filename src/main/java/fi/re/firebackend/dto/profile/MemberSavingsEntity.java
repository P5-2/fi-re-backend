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
public class MemberSavingsEntity {
    private String username; //회원 ID
    private String goalName; // 목표 이름
    private String finPrdtCd; // 상품 코드
    private Date startDate; // 가입일
    private Date endDate; // 만기일
    private int savedAmount; // 지금까지 저금한 금액
    private int monthlyDeposit; // 이번 달 입금 금액
    private int targetAmount; //목표 금액
}
