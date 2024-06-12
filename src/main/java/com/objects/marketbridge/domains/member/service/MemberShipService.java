package com.objects.marketbridge.domains.member.service;

import com.objects.marketbridge.common.kakao.KakaoPayService;
import com.objects.marketbridge.common.kakao.dto.*;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.Membership;
import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.member.dto.CreateSubsDto;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.member.service.port.MembershipRepository;
import com.objects.marketbridge.domains.payment.domain.Amount;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.objects.marketbridge.common.kakao.KakaoPayConfig.SUBS_CID;

@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class MemberShipService {

    private final MembershipRepository membershipRepository;
    private final MemberRepository memberRepository;
    private final KakaoPayService kakaoPayService;

    @Transactional
    public void changeMemberShip(Long id) { //TODO WOW회원 멤버십 해지시 basic 돌리는 메서드 필요 - by 정민우
        Member findMember = memberRepository.findById(id);
        findMember.setMembership(MembershipType.WOW.toString());

        memberRepository.save(findMember);
    }

    public KakaoPayReadyResponse kakaoPayReady(KakaoPayReadyRequest request){
        return kakaoPayService.ready(request);
    }

    @Transactional
    public void savePayReadyData(CreateSubsDto createSubsDto) {
        // 1. sid 정보 저장
        membershipRepository.save(createSubscription(createSubsDto));
    }
    //정기결제 1회차 승인
    public KakaoPayApproveResponse kakaoPayApprove(String pgToken, String orderNo){
        Membership membership = membershipRepository.findBySubsOrderNo(orderNo);
        return kakaoPayService.approve(createKakaoRequest(membership, pgToken));
    }
    //정기결제 2회차 sid 이용한 승인 (스케줄러사용 : MemberShipJob)
    public KakaoPayApproveResponse kakaoPaySubsApprove(Membership membership){
        return kakaoPayService.subsApprove(createSubsApprove(membership));
    }

    @Transactional
    public void saveApprovalResponse(KakaoPayApproveResponse response){
        Membership membership = membershipRepository.findBySubsOrderNo(response.getPartnerOrderId());
        Member member = memberRepository.findById(Long.parseLong(response.getPartnerUserId()));
        membership.update(member,response.getPartnerOrderId(),response.getTid(),response.getSid(),response.getCid(),response.getOrderName(),response.getQuantity(),response.getPaymentMethodType(),response.getCardInfo(),response.getAmount(), LocalDate.now().plusMonths(1L));
    }

    @Transactional
    public void saveSubsApprovalResponse(KakaoPayApproveResponse response){
        Membership membership = membershipRepository.findBySubsOrderNo(response.getPartnerOrderId());
        membership.update(LocalDate.now().plusMonths(1L));
    }

    private Membership createSubscription(CreateSubsDto createSubsDto) {
        Member member = memberRepository.findById(createSubsDto.getMemberId());
        String subsOrderNo = createSubsDto.getSubsOrderNo();
        String tid = createSubsDto.getTid();
        Amount amount = createSubsDto.getAmout();

        return Membership.create(member,subsOrderNo, tid,amount);
    }

    private KakaoPayApproveRequest createKakaoRequest(Membership membership, String pgToken) {
        return KakaoPayApproveRequest.builder()
                .pgToken(pgToken)
                .partnerOrderId(membership.getSubsOrderNo())
                .partnerUserId(membership.getMember().getId().toString())
                .tid(membership.getTid())
                .totalAmount(membership.getAmount().getTotalAmount())
                .cid(SUBS_CID)
                .build();
    }

    private KakaoPaySubsApproveRequest createSubsApprove(Membership membership){
        return KakaoPaySubsApproveRequest.builder()
                .cid(membership.getCid())
                .sid(membership.getSid())
                .partnerOrderId(membership.getSubsOrderNo())
                .partnerUserId(membership.getMember().getId().toString())
                .itemName(membership.getItemName())
                .quantity(membership.getQuantity())
                .totalAmount(membership.getAmount().getTotalAmount())
                .taxFreeAmount(membership.getAmount().getTaxFreeAmount())
                .build();
    }
}

