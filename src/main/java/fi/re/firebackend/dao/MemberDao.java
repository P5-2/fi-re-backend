package fi.re.firebackend.dao;

import fi.re.firebackend.dto.login.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MemberDao {
    int NaverLogin(MemberDto memberDto);
}
