package com.objects.marketbridge.domains.coupon.infra;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
    @Query("select c from Coupon c join fetch c.memberCoupons mc where c.id = :id")
    Optional<Coupon> findByIdWithMemberCoupons(@Param(value = "id") Long id);

    @Query("select c from Coupon c join fetch c.product p where c.product.id = :productId")
    List<Coupon> findByProductId(@Param(value = "productId") Long productId);

    @Query("select c from Coupon c where c.productGroupId = :productGroupId and c.count > 0")
    List<Coupon> findByProductGroupId(@Param(value = "productGroupId") Long productGroupId);

    @Query("select c from Coupon c join fetch c.memberCoupons mc where c.productGroupId = :productGroupId and c.count > 0")
    List<Coupon> findByProductGroupIdWithMemberCoupons(@Param(value = "productGroupId") Long productGroupId);
}
