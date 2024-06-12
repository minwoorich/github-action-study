package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.domains.order.service.dto.ConfirmReturnDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ConfirmReturnHttp {

    @Getter
    @NoArgsConstructor
    public static class Request {

        @NotNull
        private Long orderDetailId;
        @NotNull
        private Long numberOfReturns;
        @NotNull
        private String reason;

        @Builder
        private Request(Long orderDetailId, Long numberOfReturns, String reason) {
            this.orderDetailId = orderDetailId;
            this.numberOfReturns = numberOfReturns;
            this.reason = reason;
        }

        public ConfirmReturnDto.Request toDto() {
            return ConfirmReturnDto.Request.builder()
                    .orderDetailId(this.orderDetailId)
                    .numberOfReturns(this.numberOfReturns)
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
        private LocalDateTime returnedDate; // 주문 취소 일자
        private ProductInfo returnedItem;
        private RefundInfo refundInfo;

        @Builder
        private Response(Long orderId, String orderNo, Long totalPrice, LocalDateTime returnedDate, RefundInfo refundInfo, ProductInfo returnedItem) {
            this.orderId = orderId;
            this.orderNo = orderNo;
            this.totalPrice = totalPrice;
            this.returnedDate = returnedDate;
            this.refundInfo = refundInfo;
            this.returnedItem = returnedItem;
        }

        public static Response of(ConfirmReturnDto.Response dto) {
            return Response.builder()
                    .orderId(dto.getOrderId())
                    .orderNo(dto.getOrderNo())
                    .totalPrice(dto.getTotalPrice())
                    .returnedDate(dto.getReturnedDate())
                    .refundInfo(RefundInfo.of(dto.getRefundInfo()))
                    .returnedItem(ProductInfo.of(dto.getReturnedItem()))
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

        public static ProductInfo of(ConfirmReturnDto.ProductInfo dto) {
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

        public static RefundInfo of(ConfirmReturnDto.RefundInfo dto) {
            return RefundInfo.builder()
                    .totalRefundAmount(dto.getTotalRefundAmount())
                    .refundMethod(dto.getRefundMethod())
                    .refundProcessedAt(dto.getRefundProcessedAt())
                    .build();
        }
    }

}
