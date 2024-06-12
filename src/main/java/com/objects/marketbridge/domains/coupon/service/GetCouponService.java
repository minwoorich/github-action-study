package com.objects.marketbridge.domains.coupon.service;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.service.dto.GetCouponDto;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class GetCouponService {

    private final CouponRepository couponRepository;

    @Builder
    public GetCouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<GetCouponDto> findCouponsForProductGroup(Long productGroupId) {

        List<Coupon> coupons = couponRepository.findByProductGroupId(productGroupId);

        if (coupons.isEmpty()) {
            return new ArrayList<>();
        }
        return coupons.stream()
                .map(GetCouponDto::of) // Coupon -> GetCouponDto
                .toList();
    }

    public GetCouponDto find(Long id) {
        return GetCouponDto.of(couponRepository.findById(id));
    }
}
