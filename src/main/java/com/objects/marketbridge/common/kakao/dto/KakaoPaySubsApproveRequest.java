package com.objects.marketbridge.common.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
public class KakaoPaySubsApproveRequest {

    private String cid;
    private String sid;

    @JsonProperty("partner_order_id")
    private String partnerOrderId;

    @JsonProperty("partner_user_id")
    private String partnerUserId;

    @JsonProperty("item_name")
    private String itemName;

    private Long quantity;

    @JsonProperty("total_amount")
    private Long totalAmount;

    @JsonProperty("tax_free_amount")
    private Long taxFreeAmount;


    @Builder
    public KakaoPaySubsApproveRequest(String cid, String sid, String partnerOrderId, String partnerUserId, String itemName, Long quantity, Long totalAmount, Long taxFreeAmount) {
        this.cid = cid;
        this.sid = sid;
        this.partnerOrderId = partnerOrderId;
        this.partnerUserId = partnerUserId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.taxFreeAmount = taxFreeAmount;
    }

    public MultiValueMap<String, String> toMultiValueMap() {

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();

        requestMap.add("cid", cid);
        requestMap.add("sid", sid);
        requestMap.add("partner_order_id", partnerOrderId);
        requestMap.add("partner_user_id",  partnerUserId);
        requestMap.add("item_name",  itemName);
        requestMap.add("quantity",  quantity.toString());
        requestMap.add("total_amount",  totalAmount.toString());
        requestMap.add("tax_free_amount",  taxFreeAmount.toString());

        return requestMap;
    }
}
