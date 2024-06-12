package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.domains.order.service.dto.RequestReturnDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RequestReturnHttp {

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

        public static Response of(RequestReturnDto.Response dto) {
            return Response.builder()
                    .productInfo(ProductInfo.of(dto.getProductInfo()))
                    .returnRefundInfo(ReturnRefundInfo.of(dto.getReturnRefundInfo()))
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

        public static ProductInfo of(RequestReturnDto.ProductInfo dto) {
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

        public static ReturnRefundInfo of(RequestReturnDto.ReturnRefundInfo serviceDto) {
            return ReturnRefundInfo.builder()
                    .deliveryFee(serviceDto.getDeliveryFee())
                    .returnFee(serviceDto.getReturnFee())
                    .productTotalPrice(serviceDto.getProductTotalPrice())
                    .build();
        }
    }

}
