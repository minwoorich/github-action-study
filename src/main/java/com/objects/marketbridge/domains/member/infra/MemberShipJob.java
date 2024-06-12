package com.objects.marketbridge.domains.member.infra;

import com.objects.marketbridge.common.kakao.dto.KakaoPayApproveResponse;
import com.objects.marketbridge.domains.member.domain.Membership;
import com.objects.marketbridge.domains.member.service.MemberShipService;
import com.objects.marketbridge.domains.member.service.port.MembershipRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class MemberShipJob {

    private final MemberShipService memberShipService;
    private final MembershipRepository membershipRepository;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽3시 0분 0초
//    @Scheduled(cron = "0/30 * * * * ?") // 30초마다 수행
    public void subscriptionPaymentJob() {

        // 1) 오늘이 결제일인 membership들 전부 조회
        List<Membership> memberships = membershipRepository.findByNextBillingDateEquals(LocalDate.now());

        // 2) 정기결제 배치 실행
        memberships.forEach(m -> {
            KakaoPayApproveResponse kakaoPayApproveResponse = memberShipService.kakaoPaySubsApprove(m);
            memberShipService.saveSubsApprovalResponse(kakaoPayApproveResponse);
        });
    }
}
