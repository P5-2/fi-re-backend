package fi.re.firebackend.dto.member;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private String username;    // snsUser ID
    private String platform;    // API 유형(kakao/naver)
    private String nickname;    // 닉네임
    private String age;         // 나이
    private int salary;         // 급여
    private int assets;         // 자산
    private int riskPoint;      // 리스크포인트
    private int exp;            // 경험포인트
    private int goalAmount;     // 연간투자목표금액
    private String keyword;     // 키워드
    private String email;       // 이메일
}
