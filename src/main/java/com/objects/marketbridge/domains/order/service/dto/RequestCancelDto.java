package com.objects.marketbridge.domains.order.service.dto;

import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.order.domain.MemberShipPrice;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

public class RequestCancelDto {

    @Getter
    @NoArgsConstructor
    public static class Response {
        private ProductInfo productInfo;
        private CancelRefundInfo cancelRefundInfo;

        @Builder
        private Response(ProductInfo productInfo, CancelRefundInfo cancelRefundInfo) {
            this.productInfo = productInfo;
            this.cancelRefundInfo = cancelRefundInfo;
        }

        public static Response of(OrderDetail orderDetail, Long numberOfCancellation, String memberShip) {
            return Response.builder()
                    // TODO coupon 으로 인해 N+1문제 발생할 것으로 예상 (of) -> fetchJoin으로 쿠폰까지 조인후 해결
                    .productInfo(ProductInfo.of(orderDetail, numberOfCancellation))
                    .cancelRefundInfo(CancelRefundInfo.of(orderDetail, memberShip, numberOfCancellation))
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ProductInfo {
        private Long quantity;
        private String name;
        private Long price;
        private String image; // TODO 주문 취소 요청 이미지 반환

        @Builder
        private ProductInfo(Long quantity, String name, Long price, String image) {
            this.quantity = quantity;
            this.name = name;
            this.price = price;
            this.image = image;
        }

        public static ProductInfo of(OrderDetail orderDetail, Long numberOfCancellation) {
            return ProductInfo.builder()
                    .quantity(numberOfCancellation)
                    .name(orderDetail.getProduct().getName())
                    .price(orderDetail.getPrice())
                    .image(orderDetail.getProduct().getThumbImg())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CancelRefundInfo {
        private Long deliveryFee;
        private Long refundFee;
        private Long discountPrice;
        private Long totalPrice;

        @Builder
        private CancelRefundInfo(Long deliveryFee, Long refundFee, Long discountPrice, Long totalPrice) {
            this.deliveryFee = deliveryFee;
            this.refundFee = refundFee;
            this.discountPrice = discountPrice;
            this.totalPrice = totalPrice;
        }

        public static CancelRefundInfo of(OrderDetail orderDetail, String memberShip, Long numberOfCancellation) {
            if (isBasicMember(memberShip)) {
                return createDto(orderDetail, MemberShipPrice.BASIC.getDeliveryFee(), MemberShipPrice.BASIC.getRefundFee(), numberOfCancellation);
            }
            return createDto(orderDetail, MemberShipPrice.WOW.getDeliveryFee(), MemberShipPrice.WOW.getRefundFee(), numberOfCancellation);
        }

        private static boolean isBasicMember(String memberShip) {
            return Objects.equals(memberShip, MembershipType.BASIC.getText());
        }

        private static CancelRefundInfo createDto(OrderDetail orderDetail, Long deliveryFee, Long refundFee, Long quantity) {
            Long discountPrice = 0L;

            if (hasMemberCoupon(orderDetail)) {
                discountPrice = orderDetail.getMemberCoupon().getCoupon().getPrice();
            }

            return CancelRefundInfo.builder()
                    .discountPrice(discountPrice)
                    .totalPrice(Long.valueOf(orderDetail.totalAmount(quantity)))
                    .deliveryFee(deliveryFee)
                    .refundFee(refundFee)
                    .build();
        }

        private static boolean hasMemberCoupon(OrderDetail orderDetail) {
            return orderDetail.getMemberCoupon() != null;
        }
    }


}
