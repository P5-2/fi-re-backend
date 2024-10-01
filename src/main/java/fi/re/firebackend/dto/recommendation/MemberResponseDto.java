package fi.re.firebackend.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
    private String nickname;    // 닉네임
    private String age;         // 나이
    private int salary;         // 급여
    private int assets;         // 자산
    private int riskPoint;      // 리스크포인트
    private int exp;            // 경험포인트
    private int goalAmount;     // 연간투자목표금액
    private String keyword;     // 키워드
}
