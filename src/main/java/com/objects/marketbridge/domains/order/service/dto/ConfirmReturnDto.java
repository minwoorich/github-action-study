package com.objects.marketbridge.domains.order.service.dto;

import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.payment.service.dto.RefundDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ConfirmReturnDto {

    @Getter
    @NoArgsConstructor
    public static class Request {

        private Long orderDetailId;
        private Long numberOfReturns;
        private String reason;

        @Builder
        private Request(Long orderDetailId, Long numberOfReturns, String reason) {
            this.orderDetailId = orderDetailId;
            this.numberOfReturns = numberOfReturns;
            this.reason = reason;
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

        public static Response of(OrderDetail orderDetail, RefundDto refundDto) {
            return Response.builder()
                    .orderId(orderDetail.getOrder().getId())
                    .orderNo(orderDetail.getOrderNo())
                    .totalPrice(Long.valueOf(orderDetail.cancelAmount()))
                    .returnedDate(orderDetail.getCancelledAt())
                    .refundInfo(RefundInfo.of(refundDto))
                    .returnedItem(ProductInfo.of(orderDetail))
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
                    .name(orderDetail.getProduct().getName())
                    .price(orderDetail.getPrice())
                    .quantity(orderDetail.getReducedQuantity())
                    .productNo(orderDetail.getProduct().getProductNo())
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
        public RefundInfo(Long totalRefundAmount, String refundMethod, LocalDateTime refundProcessedAt) {
            this.totalRefundAmount = totalRefundAmount;
            this.refundMethod = refundMethod;
            this.refundProcessedAt = refundProcessedAt;
        }

        public static RefundInfo of(RefundDto refundDto) {
            return RefundInfo.builder()
                    .totalRefundAmount(refundDto.getTotalRefundAmount())
                    .refundMethod(refundDto.getRefundMethod())
                    .refundProcessedAt(refundDto.getRefundProcessedAt())
                    .build();
        }
    }
}
