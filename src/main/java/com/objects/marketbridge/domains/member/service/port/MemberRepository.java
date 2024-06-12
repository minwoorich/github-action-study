package com.objects.marketbridge.domains.member.service.port;

import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.dto.GetMemberInfoWithPassword;
import com.objects.marketbridge.domains.member.dto.MemberEmail;
import com.objects.marketbridge.domains.member.dto.MemberId;

import java.util.List;

public interface MemberRepository {

    boolean existsByEmail(String email);

    Member findByEmail(String email);

    Member findById(Long id);

    Member save(Member member);

    void saveAll(List<Member> members);

    void deleteAllInBatch();

    GetMemberInfoWithPassword getMemberInfoById(Long memberId);

    MemberEmail getEmailById(Long memberId);

    MemberId findIdByNameAndEmail(String name, String email);

    MemberEmail findEmailByNameAndPhoneNo(String name, String phoneNo);

}
