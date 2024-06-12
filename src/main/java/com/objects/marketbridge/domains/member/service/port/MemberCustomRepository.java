package com.objects.marketbridge.domains.member.service.port;

import com.objects.marketbridge.domains.member.domain.Member;

public interface MemberCustomRepository {
    //JPA가 지원하지 않는 기능

    Member findByIdWithAddresses(Long id);

}
