package fi.re.firebackend.dto.recommendation;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

//추천을 위한 member VO
@Data
public class MemberEntity {

    private String username;   // SNS 사용자 ID
    private int age;           // 나이
    private int salary;        // 급여
    private int assets;        // 자산
    private int riskPoint;     // 리스크 포인트
    private int goalAmount;    // 연간 투자 목표 금액
    private String keyword;     // 관련 키워드
    private List<String> keywordList; // 분리된 키워드 리스트

    // 키워드 분리 메서드
    public void parseKeywords() {
        if (keyword != null && !keyword.isEmpty()) {
            this.keywordList = Arrays.asList(keyword.split(","));
        }
    }
}
