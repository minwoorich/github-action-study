package com.objects.marketbridge.domains.order.service.dto;

import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.order.domain.MemberShipPrice;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

public class RequestReturnDto {

    @Getter
    @NoArgsConstructor
    public static class Response {
        private ProductInfo productInfo;
        private ReturnRefundInfo returnRefundInfo;

        @Builder
        private Response(ProductInfo productInfo, ReturnRefundInfo returnRefundInfo) {
            this.productInfo = productInfo;
            this.returnRefundInfo = returnRefundInfo;
        }

        public static Response of(OrderDetail orderDetail, Long numberOfReturns, String membership) {
            return Response.builder()
                    .productInfo(ProductInfo.of(orderDetail, numberOfReturns))
                    .returnRefundInfo(ReturnRefundInfo.of(orderDetail, numberOfReturns, membership))
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

        public static ProductInfo of(OrderDetail orderDetail, Long numberOfReturns) {
            return ProductInfo.builder()
                    .quantity(numberOfReturns)
                    .name(orderDetail.getProduct().getName())
                    .price(orderDetail.getPrice())
                    .image(orderDetail.getProduct().getThumbImg())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ReturnRefundInfo {
        private Long deliveryFee;
        private Long returnFee;
        private Long productTotalPrice;

        @Builder
        private ReturnRefundInfo(Long deliveryFee, Long returnFee, Long productTotalPrice) {
            this.deliveryFee = deliveryFee;
            this.returnFee = returnFee;
            this.productTotalPrice = productTotalPrice;
        }

        public static ReturnRefundInfo of(OrderDetail orderDetail, Long quantity, String memberShip) {
            if (isBasicMember(memberShip)) {
                return createDto(orderDetail, MemberShipPrice.BASIC.getDeliveryFee(), MemberShipPrice.BASIC.getReturnFee(), quantity);
            }
            return createDto(orderDetail, MemberShipPrice.WOW.getDeliveryFee(), MemberShipPrice.WOW.getReturnFee(), quantity);
        }

        private static boolean isBasicMember(String memberShip) {
            return Objects.equals(memberShip, MembershipType.BASIC.getText());
        }

        private static ReturnRefundInfo createDto(OrderDetail orderDetail, Long deliveryFee, Long refundFee, Long quantity) {
            return ReturnRefundInfo.builder()
                    .deliveryFee(deliveryFee)
                    .returnFee(refundFee)
                    .productTotalPrice(Long.valueOf(orderDetail.totalAmount(quantity)))
                    .build();
        }
    }

}
