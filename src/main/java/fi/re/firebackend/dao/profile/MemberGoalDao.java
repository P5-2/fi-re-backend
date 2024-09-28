package fi.re.firebackend.dao.profile;

import fi.re.firebackend.dto.profile.MemberGoalDto;
import org.apache.ibatis.annotations.Param;

public interface MemberGoalDao {
    // 회원의 예적금 정보 조회
    MemberGoalDto getGoalInfoByUsername(@Param("username") String username);

    // 예적금 정보 저장
    void insertMemberGoal(MemberGoalDto memberGoalDto);

    // 예적금 정보 수정
    void updateMemberGoal(MemberGoalDto memberGoalDto);

    // 예적금 정보 삭제
    void deleteMemberGoal(@Param("username") String username);
}
