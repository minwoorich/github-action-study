package com.objects.marketbridge.domains.coupon.infra;

import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberCouponJpaRepository extends JpaRepository<MemberCoupon, Long> {

    @Query("SELECT mc FROM MemberCoupon mc JOIN FETCH mc.member m JOIN FETCH mc.coupon c WHERE m.id = :memberId")
    List<MemberCoupon> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT mc FROM MemberCoupon mc JOIN FETCH mc.member m JOIN FETCH mc.coupon c WHERE m.id = :memberId AND c.id = :couponId")
    Optional<MemberCoupon> findByMemberIdAndCouponId(@Param("memberId") Long memberId, @Param("couponId") Long couponId);

    @Query("SELECT mc FROM MemberCoupon mc WHERE mc.member.id = :memberId AND mc.coupon.id = :couponId AND mc.coupon.product.id = :productId")
    Optional<MemberCoupon> findByMemberIdAndCouponIdAndProductId(@Param("memberId") Long memberId, @Param("couponId") Long couponId, @Param("productId") Long productId);

}
