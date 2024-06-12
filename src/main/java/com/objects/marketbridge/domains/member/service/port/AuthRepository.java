package com.objects.marketbridge.domains.member.service.port;

import com.objects.marketbridge.domains.member.dto.AuthMember;

public interface AuthRepository {
    AuthMember findAuthMemberByEmail(String email);
}
