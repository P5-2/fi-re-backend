package fi.re.firebackend.dao.profile;

import fi.re.firebackend.dto.profile.EachTransaction;
import fi.re.firebackend.dto.profile.MemberSavingsEntity;
import fi.re.firebackend.dto.profile.MemberSavingsResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProfileDao {
    // 회원의 예적금 갯수 조회
    int countMemberSavings(@Param("username") String username);

    // 회원의 모든 예적금 목록 조회
    List<MemberSavingsResponseDto> getAllMemberSavings(@Param("username") String username);

    // 예적금 데이터 삽입
    int insertMemberSavings(MemberSavingsEntity memberSavingsEntity);

    // 예적금 데이터 업데이트
    int updateMemberSavings(MemberSavingsEntity memberSavingsEntity);

    // 멤버의 예적금 삭제하기
    int deleteMemberSavings(@Param("username") String username, @Param("finPrdtCd") String finPrdtCd);

    // 이번달, 총 액수
    String getMemberSavingsName(@Param("username") String username, @Param("prdNo") String prdNo);
    EachTransaction fetchAmount(@Param("username") String username, @Param("goalName") String goalName);
    int fetchMemberSavings(EachTransaction eachTransaction);
}
