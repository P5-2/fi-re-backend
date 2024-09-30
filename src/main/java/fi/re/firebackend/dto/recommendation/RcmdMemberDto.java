package fi.re.firebackend.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RcmdMemberDto {
    private String nickname;    // 닉네임
    private String age;         // 나이
    private int salary;         // 급여
    private int assets;         // 자산
    private int riskPoint;      // 리스크포인트
    private String keyword;     // 키워드
}
