package com.objects.marketbridge.domains.member.service;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.coupon.service.port.MemberCouponRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class RegisterCouponService {

    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;


    @Builder
    public RegisterCouponService(MemberRepository memberRepository, CouponRepository couponRepository, MemberCouponRepository memberCouponRepository) {
        this.memberRepository = memberRepository;
        this.couponRepository = couponRepository;
        this.memberCouponRepository = memberCouponRepository;
    }

    @Transactional
    public Coupon registerCouponToMember(Long memberId, Long couponId) {

        Optional<MemberCoupon> memberCoupon = memberCouponRepository.findByMemberIdAndCouponIdOptional(memberId, couponId);

        if (memberCoupon.isPresent()) {
            throw CustomLogicException.createBadRequestError(ErrorCode.DUPLICATE_ERROR, "이미 등록된 쿠폰 입니다");
        }

        Member member = memberRepository.findById(memberId);
        Coupon coupon = couponRepository.findById(couponId);

        coupon.decreaseCount();
        coupon.addMemberCoupon(MemberCoupon.create(member, coupon));

        return couponRepository.save(coupon);
    }
}
