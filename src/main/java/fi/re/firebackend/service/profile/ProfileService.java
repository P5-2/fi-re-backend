package fi.re.firebackend.service.profile;

import fi.re.firebackend.dao.member.MemberDaotwo;
import fi.re.firebackend.dto.member.MemberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final MemberDaotwo memberDaotwo;

    @Autowired
    public ProfileService(MemberDaotwo memberDaotwo) {
        this.memberDaotwo = memberDaotwo;
    }

    public MemberDto getProfile(String username) {
        return memberDaotwo.findMember(username);
    }
}
