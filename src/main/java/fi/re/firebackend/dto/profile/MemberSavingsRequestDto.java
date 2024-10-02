package fi.re.firebackend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSavingsRequestDto { // 프론트에서 서버로 요청 보내는 내용
    private String goalName; // 목표 이름
    private String finPrdtCd; // 상품 코드(prdNo)
    private Date startDate; // 가입일
    private int savedAmount; // 지금까지 저금한 금액
    private int monthlyDeposit; // 이번 달 입금 금액
    private int saveTrm; // 가입 기간(이거랑 가입일 이용해서 enddate 구하면 됨)
    private int targetAmount; //목표 금액
}
