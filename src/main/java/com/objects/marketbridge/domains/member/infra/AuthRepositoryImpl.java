package com.objects.marketbridge.domains.member.infra;

import com.objects.marketbridge.domains.member.dto.AuthMember;
import com.objects.marketbridge.domains.member.service.port.AuthRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {

    private final MemberJpaRepository memberJpaRepository;
    @Override
    public AuthMember findAuthMemberByEmail(String email) {
        return  memberJpaRepository.findAuthMemberByEmail(email).orElseThrow(EntityNotFoundException::new);
    }
}
