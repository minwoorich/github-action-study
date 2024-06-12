package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.domains.order.service.dto.ConfirmCancelDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ConfirmCancelHttp {

    @Getter
    @NoArgsConstructor
    public static class Request {

        @NotNull
        private Long orderDetailId;
        @NotNull
        private Long numberOfCancellation;
        @NotNull
        private String reason;

        @Builder
        public Request(Long orderDetailId, Long numberOfCancellation, String reason) {
            this.orderDetailId = orderDetailId;
            this.numberOfCancellation = numberOfCancellation;
            this.reason = reason;
        }

        public ConfirmCancelDto.Request toDto() {
            return ConfirmCancelDto.Request.builder()
                    .orderDetailId(this.orderDetailId)
                    .numberOfCancellation(this.numberOfCancellation)
                    .reason(this.reason)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long orderId;
        private String orderNo;
        private Long totalPrice;
        private LocalDateTime cancellationDate; // 주문 취소 일자
        private ProductInfo cancelledItem;
        private RefundInfo refundInfo;

        @Builder
        private Response(Long orderId, String orderNo, Long totalPrice, LocalDateTime cancellationDate, RefundInfo refundInfo, ProductInfo cancelledItem) {
            this.orderId = orderId;
            this.orderNo = orderNo;
            this.totalPrice = totalPrice;
            this.cancellationDate = cancellationDate;
            this.refundInfo = refundInfo;
            this.cancelledItem = cancelledItem;
        }

        public static Response of(ConfirmCancelDto.Response dto) {
            return Response.builder()
                    .orderId(dto.getOrderId())
                    .orderNo(dto.getOrderNo())
                    .totalPrice(dto.getTotalPrice())
                    .cancellationDate(dto.getCancellationDate())
                    .refundInfo(RefundInfo.of(dto.getRefundInfo()))
                    .cancelledItem(ProductInfo.of(dto.getCancelledItem()))
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

        public static ProductInfo of(ConfirmCancelDto.ProductInfo dto) {
            return ProductInfo.builder()
                    .productId(dto.getProductId())
                    .name(dto.getName())
                    .price(dto.getPrice())
                    .quantity(dto.getQuantity())
                    .productNo(dto.getProductNo())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class RefundInfo {

        private Long totalRefundAmount;
        private String refundMethod;
        private LocalDateTime refundProcessedAt; // 환불 일자

        @Builder
        private RefundInfo(Long totalRefundAmount, String refundMethod, LocalDateTime refundProcessedAt) {
            this.totalRefundAmount = totalRefundAmount;
            this.refundMethod = refundMethod;
            this.refundProcessedAt = refundProcessedAt;
        }

        public static RefundInfo of(ConfirmCancelDto.RefundInfo dto) {
            return RefundInfo.builder()
                    .totalRefundAmount(dto.getTotalRefundAmount())
                    .refundMethod(dto.getRefundMethod())
                    .refundProcessedAt(dto.getRefundProcessedAt())
                    .build();
        }
    }
}
