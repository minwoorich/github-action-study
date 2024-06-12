package com.objects.marketbridge.domains.order.domain;

import com.objects.marketbridge.domains.member.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCancelReturn extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_cancel_return_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id")
    private OrderDetail orderDetail;

    private String statusCode;

    private Long quantity;

    private String reason;

    private Long refundAmount;

    private String previousStateCode;

    @Builder
    public OrderCancelReturn(OrderDetail orderDetail, String statusCode, Long quantity, String reason, Long refundAmount, String previousStateCode) {
        this.orderDetail = orderDetail;
        this.statusCode = statusCode;
        this.quantity = quantity;
        this.reason = reason;
        this.refundAmount = refundAmount;
        this.previousStateCode = previousStateCode;
    }

    public static OrderCancelReturn create(OrderDetail orderDetail, CancelReturnStatusCode statusInfo, String reason) {
        return OrderCancelReturn.builder()
                .orderDetail(orderDetail)
                .quantity(orderDetail.getReducedQuantity())
                .reason(reason)
                .statusCode(statusInfo.getStatusCode())
                .refundAmount(Long.valueOf(orderDetail.cancelAmount()))
                .previousStateCode(statusInfo.getPreviousStatusCode())
                .build();
    }

}
