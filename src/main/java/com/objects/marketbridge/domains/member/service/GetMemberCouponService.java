package com.objects.marketbridge.domains.member.service;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.service.port.MemberCouponRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GetMemberCouponService {
    private final MemberCouponRepository memberCouponRepository;

    @Builder
    public GetMemberCouponService(MemberCouponRepository memberCouponRepository) {
        this.memberCouponRepository = memberCouponRepository;
    }

    public List<Coupon> findCouponsForMember(Long memberId) {
        List<MemberCoupon> memberCoupons = memberCouponRepository.findByMemberId(memberId);
        return memberCoupons.stream()
                .filter(mc -> !mc.getIsUsed())
                .map(MemberCoupon::getCoupon)
                .collect(Collectors.toList());
    }
}
