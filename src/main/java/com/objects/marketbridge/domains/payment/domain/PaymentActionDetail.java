package com.objects.marketbridge.domains.payment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentActionDetail {
    private String aid;

    @JsonProperty("approved_at")
    private String approvedAt;
    private Long amount;

    @JsonProperty("discount_amount")
    private Long discountAmount;

    @JsonProperty("payment_action_type")
    private String paymentActionType;
    private String payload;

    @Builder
    public PaymentActionDetail(String aid, String approvedAt, Long amount, Long discountAmount, String paymentActionType, String payload) {
        this.aid = aid;
        this.approvedAt = approvedAt;
        this.amount = amount;
        this.discountAmount = discountAmount;
        this.paymentActionType = paymentActionType;
        this.payload = payload;
    }
}
