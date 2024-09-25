package fi.re.firebackend.dao.survey;



import fi.re.firebackend.dto.member.MemberDto;


public interface SurveyDao {
    void updateSurveyResult(MemberDto memberDto);
}
