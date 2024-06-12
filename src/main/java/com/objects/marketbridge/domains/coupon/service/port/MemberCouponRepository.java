package com.objects.marketbridge.domains.coupon.service.port;

import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository {
    MemberCoupon findById(Long id);

    MemberCoupon findByMemberIdAndCouponId(Long memberId, Long couponId);

    Optional<MemberCoupon> findByMemberIdAndCouponIdOptional(Long memberId, Long couponId);

    List<MemberCoupon> findByMemberId(Long memberId);

    MemberCoupon findByMemberIdAndCouponIdAndProductId(Long memberId, Long couponId, Long productId);

    MemberCoupon save(MemberCoupon memberCoupon);

    void saveAll(List<MemberCoupon> memberCoupons);

    List<MemberCoupon> findAll();

    void deleteAllInBatch();


}
