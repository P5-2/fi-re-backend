package fi.re.firebackend.dao.login;

import fi.re.firebackend.dto.login.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface NaverLoginDao {
    int NaverLogin(MemberDto memberDto);
}
