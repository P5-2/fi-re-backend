package fi.re.firebackend.dto.member;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private String username;
    private String platform;
    private String nickname;
    private String age;
    private int salary;
    private int assets;
    private int riskPoint;
    private int exp;
    private int goalAmount;
    private String keyword;
    private String email;
}
