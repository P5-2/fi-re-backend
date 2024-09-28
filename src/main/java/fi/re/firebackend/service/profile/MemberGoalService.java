package fi.re.firebackend.service.profile;

import fi.re.firebackend.dao.profile.MemberGoalDao;
import fi.re.firebackend.dto.profile.MemberGoalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberGoalService {

    @Autowired
    private MemberGoalDao memberGoalDao;

    // 특정 회원의 예적금 정보 조회
    public MemberGoalDto getGoalInfoByUsername(String username) {
        return memberGoalDao.getGoalInfoByUsername(username);
    }

    // 새로운 예적금 정보 추가
    public void saveMemberGoal(MemberGoalDto memberGoalDto) {
        memberGoalDao.insertMemberGoal(memberGoalDto);
    }

    // 예적금 정보 수정
    public void updateMemberGoal(MemberGoalDto memberGoalDto) {
        memberGoalDao.updateMemberGoal(memberGoalDto);
    }

    // 특정 회원의 예적금 정보 삭제
    public void deleteMemberGoal(String username) {
        memberGoalDao.deleteMemberGoal(username);
    }
}
