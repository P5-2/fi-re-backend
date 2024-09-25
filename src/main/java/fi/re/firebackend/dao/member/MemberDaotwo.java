package fi.re.firebackend.dao.member;

import fi.re.firebackend.dto.member.MemberDto;

public interface MemberDaotwo {
 void insertMember(MemberDto member);
 MemberDto findMember(String username);
}
