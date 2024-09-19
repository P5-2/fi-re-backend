package fi.re.firebackend.service.login;

import fi.re.firebackend.dao.login.NaverLoginDao;
import fi.re.firebackend.dto.login.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NaverLoginService {

    private final NaverLoginDao dao;

    public NaverLoginService(NaverLoginDao dao) {
        this.dao = dao;
    }

    public boolean addMember(MemberDto dto) {
        return dao.NaverLogin(dto) > 0;
    }
}
