package fi.re.firebackend.service.survey;

import fi.re.firebackend.dao.member.MemberDaotwo;
import fi.re.firebackend.dao.survey.SurveyDao;
import fi.re.firebackend.dto.member.MemberDto;
import fi.re.firebackend.dto.survey.SurveyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SurveyService {

    private final MemberDaotwo memberDaotwo;
    private final SurveyDao surveyDao;

    @Autowired
    public SurveyService(MemberDaotwo memberDaotwo, SurveyDao surveyDao) {
        this.memberDaotwo = memberDaotwo;
        this.surveyDao = surveyDao;
    }



    public void insertSurveyResult(SurveyDto surveyDto,String username) {

        if (username == null || username.isEmpty()) {
            username = generateRandomUsername(); // 랜덤 username 생성
        }

        MemberDto member = findMember(username);

        if (member != null) {
            // 멤버가 존재하면 업데이트 로직
            member.setAge(surveyDto.getAge());
            member.setKeyword(String.join(", ", surveyDto.getKeywords()));
            member.setRiskPoint(surveyDto.getTotalScore());
            member.setAssets(surveyDto.getAssets());
            member.setSalary(surveyDto.getSalary());
            surveyDao.updateSurveyResult(member);
        } else {
            // 멤버가 존재하지 않으면 빈 멤버 생성 후 데이터 삽입
            MemberDto newMember = new MemberDto();
            newMember.setUsername(username);
            newMember.setPlatform(""); // 비워둠
            newMember.setNickname(""); // 비워둠
            newMember.setAge(surveyDto.getAge()); // 프론트에서 받은 데이터
            newMember.setSalary(surveyDto.getSalary()); // 초기값
            newMember.setAssets(surveyDto.getAssets()); // 초기값
            newMember.setRiskPoint(surveyDto.getTotalScore()); // 초기값
            newMember.setExp(0); // 초기값
            newMember.setGoalAmount(0); // 초기값
            newMember.setKeyword(String.join(", ", surveyDto.getKeywords())); // 프론트에서 받은 데이터
            newMember.setEmail(""); // 비워둠
            memberDaotwo.insertMember(newMember); // DAO에서 insertMember 메서드 호출
        }

    }

    public MemberDto findMember(String username) {
        return memberDaotwo.findMember(username);
    }

    private String generateRandomUsername() {
        return "user_" + UUID.randomUUID().toString(); // 랜덤 username 생성
    }
}
