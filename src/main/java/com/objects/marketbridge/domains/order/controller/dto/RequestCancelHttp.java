package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.domains.order.service.dto.RequestCancelDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RequestCancelHttp {

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

        public static Response of(RequestCancelDto.Response dto) {
            return Response.builder()
                    .productInfo(ProductInfo.of(dto.getProductInfo()))
                    .cancelRefundInfo(CancelRefundInfo.of(dto.getCancelRefundInfo()))
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

        public static ProductInfo of(RequestCancelDto.ProductInfo dto) {
            return ProductInfo.builder()
                    .quantity(dto.getQuantity())
                    .name(dto.getName())
                    .price(dto.getPrice())
                    .image(dto.getImage())
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

        public static CancelRefundInfo of(RequestCancelDto.CancelRefundInfo serviceDto) {
            return CancelRefundInfo.builder()
                    .deliveryFee(serviceDto.getDeliveryFee())
                    .refundFee(serviceDto.getRefundFee())
                    .discountPrice(serviceDto.getDiscountPrice())
                    .totalPrice(serviceDto.getTotalPrice())
                    .build();
        }
    }


}
