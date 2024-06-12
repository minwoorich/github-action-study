package com.objects.marketbridge.domains.payment.domain;

import com.objects.marketbridge.domains.member.domain.BaseEntity;
import com.objects.marketbridge.domains.order.domain.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE payment SET deleted_at = now() WHERE payment_id = ?")
@SQLRestriction("deleted_at is NULL")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String tid;
    private String orderNo;
    private String paymentMethod; // CARD, MONEY
    private LocalDateTime approvedAt;

    // 카드 결제
    @Embedded
    private CardInfo cardInfo;

    // 결제 금액 정보
    @Embedded
    private Amount amount;

    @Builder
    public Payment(Order order, String orderNo, String paymentMethod, String tid, CardInfo cardInfo, Amount amount, LocalDateTime approvedAt) {
        this.order = order;
        this.orderNo = orderNo;
        this.paymentMethod = paymentMethod;
        this.tid = tid;
        this.cardInfo = cardInfo;
        this.amount = amount;
        this.approvedAt = approvedAt;
    }

    public static Payment create(String orderNo, String paymentMethod, String tid, CardInfo cardInfo, Amount amount, LocalDateTime approvedAt) {
        return Payment.builder()
                .orderNo(orderNo)
                .paymentMethod(paymentMethod)
                .tid(tid)
                .cardInfo(cardInfo)
                .amount(amount)
                .approvedAt(approvedAt)
                .build();
    }

    public void linkOrder(Order order) {
        this.order = order;
    }

    public void changeStatusCode(String statusCode) {
        order.changeStatusCode(statusCode);
    }
}
