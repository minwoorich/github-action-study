package com.objects.marketbridge.domains.member.service.port;

import com.objects.marketbridge.domains.member.domain.Membership;

import java.time.LocalDate;
import java.util.List;

public interface MembershipRepository {

    Membership save(Membership membership);

    Membership findById(Long id);

    Membership findBySubsOrderNo(String subsOrderNo);

    void saveAll(List<Membership> memberships);

    List<Membership> findByNextBillingDateEquals(LocalDate today);

}
