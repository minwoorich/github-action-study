package com.objects.marketbridge.domains.member.infra;

import com.objects.marketbridge.domains.member.domain.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MembershipJpaRepository extends JpaRepository<Membership,Long> {

    Optional<Membership> findBySubsOrderNo(String subsOrderNo);

    List<Membership> findByNextBillingDateEquals(LocalDate today);

}
