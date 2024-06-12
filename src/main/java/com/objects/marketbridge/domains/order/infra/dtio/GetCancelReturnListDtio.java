package com.objects.marketbridge.domains.order.infra.dtio;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class GetCancelReturnListDtio {

    @Getter
    @NoArgsConstructor
    public static class Response {
        private LocalDateTime cancelReceiptDate;
        private LocalDateTime orderDate;
        private OrderDetailInfo orderDetailInfo;

        @Builder
        @QueryProjection
        public Response(LocalDateTime cancelReceiptDate, LocalDateTime orderDate, OrderDetailInfo orderDetailInfo) {
            this.cancelReceiptDate = cancelReceiptDate;
            this.orderDate = orderDate;
            this.orderDetailInfo = orderDetailInfo;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OrderDetailInfo {

        private String orderNo;
        private Long productId;
        private String productNo;
        private String name;
        private Long price;
        private Long quantity;
        private String orderStatus;

        @Builder
        @QueryProjection
        public OrderDetailInfo(String orderNo, Long productId, String productNo, String name, Long price, Long quantity, String orderStatus) {
            this.orderNo = orderNo;
            this.productId = productId;
            this.productNo = productNo;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.orderStatus = orderStatus;
        }
    }

}
