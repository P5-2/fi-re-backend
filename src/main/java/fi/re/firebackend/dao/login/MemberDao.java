package fi.re.firebackend.dao.login;

import fi.re.firebackend.dto.member.MemberDto;
import fi.re.firebackend.security.SecurityUser;
import org.apache.ibatis.annotations.Param;

public interface MemberDao {
    SecurityUser getInfo(String username);
    boolean ExistByName(@Param("name") String name);
    void save(SecurityUser member);
    SecurityUser findByName(@Param("name") String name);
    // 닉네임 찾기
    String findName(String username);
    // 멤버 테이블 추가
    void memberSave(MemberDto member);
}
