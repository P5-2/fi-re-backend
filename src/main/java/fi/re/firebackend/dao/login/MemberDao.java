package fi.re.firebackend.dao.login;

import fi.re.firebackend.security.SecurityUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MemberDao {
    SecurityUser getInfo(String username);
}
