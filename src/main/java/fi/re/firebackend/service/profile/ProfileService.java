package fi.re.firebackend.service.profile;

import fi.re.firebackend.dao.member.MemberDaotwo;
import fi.re.firebackend.dao.profile.ProfileDao;
import fi.re.firebackend.dto.member.MemberDto;
import fi.re.firebackend.dto.profile.MemberSavingsRequestDto;
import fi.re.firebackend.dto.profile.MemberSavingsEntity;
import fi.re.firebackend.dto.profile.MemberSavingsResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private static final Logger log = Logger.getLogger(ProfileService.class);
    private final MemberDaotwo memberDaotwo;
    private final ProfileDao profileDao;

    // 특정 사용자의 프로필 가져오기
    public MemberDto getProfile(String username) {
        return memberDaotwo.findMember(username);
    }


    public int addMemberSavings(String username, MemberSavingsRequestDto memberSavingsRequestDto) {
        // endDate 계산: 시작 날짜에서 저축 기간 (saveTrm)을 더한 후 Date 타입으로 변환
        LocalDate startDateLocal = memberSavingsRequestDto.getStartDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDateLocal = startDateLocal.plusMonths(memberSavingsRequestDto.getSaveTrm());
        Date endDate = Date.from(endDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // MemberSavingsEntity 생성
        MemberSavingsEntity memberSavingsEntity = MemberSavingsEntity.builder()
                .username(username)
                .goalName(memberSavingsRequestDto.getGoalName())
                .finPrdtCd(memberSavingsRequestDto.getFinPrdtCd())
                .startDate(memberSavingsRequestDto.getStartDate())
                .endDate(endDate)
                .savedAmount(memberSavingsRequestDto.getSavedAmount())
                .monthlyDeposit(memberSavingsRequestDto.getMonthlyDeposit())
                .targetAmount(memberSavingsRequestDto.getTargetAmount())
                .build();
        // 멤버 예적금 테이블에 저장
        return profileDao.insertMemberSavings(memberSavingsEntity);
    }

    // 특정 사용자의 예적금 목록 가져오기
    public List<MemberSavingsResponseDto> getMemberSavings(String username) {
        List<MemberSavingsResponseDto> memberSavings = profileDao.getAllMemberSavings(username);
        return memberSavings;
    }

    // 특정 사용자의 예적금 삭제
    public boolean deleteMemberSavings(String username, String prdNo) {
        int rowsAffected = profileDao.deleteMemberSavings(username, prdNo);
        return rowsAffected > 0;  // 삭제 성공 여부 반환
    }
}

