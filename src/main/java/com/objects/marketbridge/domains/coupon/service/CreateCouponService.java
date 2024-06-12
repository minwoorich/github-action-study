package com.objects.marketbridge.domains.coupon.service;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.service.dto.CreateCouponDto;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CreateCouponService {
    private final CouponRepository couponRepository;

    @Builder
    public CreateCouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public void create(CreateCouponDto dto) {
        Coupon coupon = dto.toEntity();
        couponRepository.save(coupon);
    }
}
