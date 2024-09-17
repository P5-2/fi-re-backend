package fi.re.firebackend.service;

import fi.re.firebackend.dao.MemberDao;
import fi.re.firebackend.dto.login.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberDao dao;

    public MemberService(MemberDao dao) {
        this.dao = dao;
    }

    public boolean addMember(MemberDto dto) {
        return dao.NaverLogin(dto) > 0;
    }
}
