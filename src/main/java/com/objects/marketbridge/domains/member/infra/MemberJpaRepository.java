package com.objects.marketbridge.domains.member.infra;

import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.dto.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//JpaRepository에서 제공되는 기본메서드 사용
public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    @Query("SELECT DISTINCT m FROM Member m JOIN FETCH m.addresses WHERE m.id = :memberId")
    Optional<Member> findByIdWithAddresses(@Param("memberId") Long id);
    
    Optional<AuthMember> findAuthMemberByEmail(String email);

    Optional<GetMemberInfoWithPassword> getMemberInfoById(Long memberId);

    Optional<MemberEmail> getEmailById(Long id);

    @Query("SELECT new com.objects.marketbridge.domains.member.dto.MemberId(m.id) FROM Member m WHERE m.name = :name AND m.email = :email")
    Optional<MemberId> findIdByNameAndEmail(@Param("name") String name, @Param("email") String email);

    Optional<MemberEmail> findEmailByNameAndPhoneNo(String name, String phoneNo);


}
