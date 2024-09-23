package fi.re.firebackend.service.login;

import fi.re.firebackend.dao.login.MemberDao;
import fi.re.firebackend.security.SecurityUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
public class MemberService {
    private final MemberDao dao;

    public MemberService(MemberDao dao) {
        this.dao = dao;
    }

    public SecurityUser getInfo(String username) {
        return dao.getInfo(username);
    }
}
