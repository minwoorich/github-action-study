package com.objects.marketbridge.domains.member.infra;

import com.objects.marketbridge.domains.member.domain.Membership;
import com.objects.marketbridge.domains.member.service.port.MembershipRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MembershipRepositoryImplTest {

    @Autowired
    MembershipRepository membershipRepository;

    @Test
    @DisplayName("정기 결제 일이 오늘(혹은 특정일)인 membership 테이블들을 전부 조회할 수 있다")
    void findByNextBillingDateEquals() {

        // given
        Membership membership1 = Membership.builder()
                .nextBillingDate(LocalDate.of(2024, 1, 1))
                .build();
        Membership membership2 = Membership.builder()
                .nextBillingDate(LocalDate.of(2024, 1, 1))
                .build();
        Membership membership3 = Membership.builder()
                .nextBillingDate(LocalDate.of(2024, 1, 2))
                .build();

        membershipRepository.saveAll(List.of(membership1, membership2, membership3));

        // when
        List<Membership> memberships = membershipRepository.findByNextBillingDateEquals(LocalDate.of(2024, 1, 1));

        // then
        Assertions.assertThat(memberships).hasSize(2);
        Assertions.assertThat(memberships).extracting(Membership::getNextBillingDate).containsExactlyInAnyOrder(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1));
    }
}