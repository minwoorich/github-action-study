package com.objects.marketbridge.common.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.objects.marketbridge.domains.payment.domain.Amount;
import com.objects.marketbridge.domains.payment.domain.CardInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class KakaoPayApproveResponse {

    private String aid;
    private String tid;
    private String cid;
    private String sid;

    @JsonProperty("partner_order_id")
    private String partnerOrderId;

    @JsonProperty("partner_user_id")
    private String partnerUserId;

    @JsonProperty("payment_method_type")
    private String paymentMethodType;

    @JsonProperty("item_name")
    private String orderName;

    private Long quantity;

    private Amount amount;

    @JsonProperty("card_info")
    private CardInfo cardInfo;

    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;

    @Builder
    public KakaoPayApproveResponse(String aid, String tid, String sid, String cid, String partnerOrderId, String partnerUserId, String paymentMethodType, Amount amount, CardInfo cardInfo, String orderName, Long quantity, LocalDateTime createdAt, LocalDateTime approvedAt, String payload) {
        this.aid = aid;
        this.tid = tid;
        this.sid = sid;
        this.cid = cid;
        this.partnerOrderId = partnerOrderId;
        this.partnerUserId = partnerUserId;
        this.paymentMethodType = paymentMethodType;
        this.amount = amount;
        this.cardInfo = cardInfo;
        this.orderName = orderName;
        this.quantity = quantity;
        this.approvedAt = approvedAt;
    }

    public static KakaoPayApproveResponse create(String aid, String tid, String sid, String cid, String partnerOrderId, String partnerUserId,  Amount amount, CardInfo cardInfo, String orderName, Long quantity, LocalDateTime createdAt, LocalDateTime approvedAt, String payload) {
        return KakaoPayApproveResponse.builder()
                .aid(aid)
                .tid(tid)
                .sid(sid)
                .cid(cid)
                .partnerOrderId(partnerOrderId)
                .partnerUserId(partnerUserId)
                .amount(amount)
                .cardInfo(cardInfo)
                .orderName(orderName)
                .quantity(quantity)
                .approvedAt(approvedAt)
                .build();
    }
}
