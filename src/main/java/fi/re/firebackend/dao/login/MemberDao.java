package fi.re.firebackend.dao.login;

import fi.re.firebackend.security.SecurityUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Mapper
@Repository
public interface MemberDao {
    SecurityUser getInfo(String username);
    boolean ExistByName(@Param("name") String name);
    void save(SecurityUser member);
    // 이메일로 회원 조회
    SecurityUser findByName(@Param("name") String name);
    // 닉네임 찾기
    String findName(String username);
}
