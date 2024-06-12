package com.objects.marketbridge.domains.order.domain;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalcTotalDiscountService{

    public Long calculate(Order order) {

        Long totalCouponPrice = calcUsedCoupon(order.getOrderDetails());
        Long totalUsedPoint = calcUsedPoint(order.getOrderDetails());
        Long memberShipDiscount = calcMembershipDiscount();
        Long additionalDiscount = calcAdditionalDiscount();

        return totalCouponPrice + totalUsedPoint + memberShipDiscount + additionalDiscount;
    }

    private Long calcUsedCoupon(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .filter(o -> o.getMemberCoupon() != null)
                .mapToLong(o -> o.getMemberCoupon().getCoupon().getPrice())
                .sum();
    }

    // TODO : 추후에 point 서비스 도입하면 이것도 구현 필요
    private Long calcUsedPoint(List<OrderDetail> orderDetails) {
        return 0L;
    }

    // TODO : 추후에 membership 할인 서비스 도입하면 이것도 구현 필요
    private Long calcMembershipDiscount() {
        return 0L;
    }

    // TODO : 추후에 기타 할인 서비스 도입하면 이것도 구현 필요
    private Long calcAdditionalDiscount() {
        return 0L;
    }

}
