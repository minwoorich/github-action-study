package com.objects.marketbridge.domains.order.service.dto;

import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.order.domain.MemberShipPrice;
import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

public class GetCancelDetailDto {

    @Getter
    @NoArgsConstructor
    public static class Response {
        private LocalDateTime orderDate;
        private LocalDateTime cancelDate;
        private String orderNo;
        private String reason;
        private ProductInfo productInfo;
        private RefundInfo refundInfo;

        @Builder
        private Response(LocalDateTime orderDate, LocalDateTime cancelDate, String orderNo, String reason, ProductInfo productInfo, RefundInfo refundInfo) {
            this.orderDate = orderDate;
            this.cancelDate = cancelDate;
            this.orderNo = orderNo;
            this.reason = reason;
            this.productInfo = productInfo;
            this.refundInfo = refundInfo;
        }

        public static Response of(OrderCancelReturn orderCancelReturn, String memberShip, DateTimeHolder dateTimeHolder) {
            return Response.builder()
                    .orderDate(dateTimeHolder.getCreateTime(orderCancelReturn.getOrderDetail().getOrder()))
                    .cancelDate(orderCancelReturn.getOrderDetail().getCancelledAt())
                    .orderNo(orderCancelReturn.getOrderDetail().getOrderNo())
                    .reason(orderCancelReturn.getReason())
                    .productInfo(ProductInfo.of(orderCancelReturn.getOrderDetail()))
                    .refundInfo(RefundInfo.of(orderCancelReturn.getOrderDetail(), memberShip))
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ProductInfo {
        private Long productId;
        private String productNo;
        private String name;
        private Long price;
        private Long quantity;

        @Builder
        private ProductInfo(Long productId, String productNo, String name, Long price, Long quantity) {
            this.productId = productId;
            this.productNo = productNo;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public static ProductInfo of(OrderDetail orderDetail) {
            return ProductInfo.builder()
                    .productId(orderDetail.getProduct().getId())
                    .productNo(orderDetail.getProduct().getProductNo())
                    .name(orderDetail.getProduct().getName())
                    .price(orderDetail.getPrice())
                    .quantity(orderDetail.getQuantity())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class RefundInfo {

        private Long deliveryFee;
        private Long refundFee;
        private Long discountPrice;
        private Long totalPrice;

        @Builder
        private RefundInfo(Long deliveryFee, Long refundFee, Long discountPrice, Long totalPrice) {
            this.deliveryFee = deliveryFee;
            this.refundFee = refundFee;
            this.discountPrice = discountPrice;
            this.totalPrice = totalPrice;
        }

        public static RefundInfo of(OrderDetail orderDetail, String memberShip) {
            if (isBasicMember(memberShip)) {
                return createDto(orderDetail, MemberShipPrice.BASIC.getDeliveryFee(), MemberShipPrice.BASIC.getRefundFee());
            }
            return createDto(orderDetail, MemberShipPrice.WOW.getDeliveryFee(), MemberShipPrice.WOW.getRefundFee());
        }

        private static boolean isBasicMember(String memberShip) {
            return Objects.equals(memberShip, MembershipType.BASIC.getText());
        }

        private static RefundInfo createDto(OrderDetail orderDetail, Long deliveryFee, Long refundFee) {
            Long discountPrice = 0L;
            if (hasMemberCoupon(orderDetail))
                discountPrice = orderDetail.getMemberCoupon().getCoupon().getPrice();

            return RefundInfo.builder()
                    .discountPrice(discountPrice)
                    .totalPrice(Long.valueOf(Objects.requireNonNull(orderDetail).cancelAmount()))
                    .refundFee(refundFee)
                    .deliveryFee(deliveryFee)
                    .build();
        }

        private static boolean hasMemberCoupon(OrderDetail orderDetail) {
            return orderDetail != null && orderDetail.getMemberCoupon() != null;
        }
    }
}
