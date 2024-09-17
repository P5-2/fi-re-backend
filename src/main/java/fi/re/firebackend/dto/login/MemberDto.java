package fi.re.firebackend.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private String username;   // 이메일
    private String platform;   // API요청지(구글/카카오)
    private String nickname;   // 닉네임
    private Integer age;       // 나이
    private Integer salary;    // 급여
    private Integer assets;    // 총자산
    private Integer riskPoint; // 위험도
    private Integer exp;       // 성장포인트
    private Integer goalAmount;// 모으기목표금액
    private String keyword;    // 키워드
}
