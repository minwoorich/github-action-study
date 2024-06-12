package com.objects.marketbridge.domains.member.controller.request;

import com.objects.marketbridge.common.kakao.dto.KakaoPayReadyRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class CreateSubsRequest {

    private Long price;

    private String name;


    @Builder
    public CreateSubsRequest(Long price, String name) {
        this.price = price;
        this.name = name;
    }

    public KakaoPayReadyRequest toKakaoReadyRequest(Long memberId, String subsOrderNo, String cid, String approvalUrl, String failUrl, String cancelUrl) {
        log.info("subsOrderNo , {}",subsOrderNo);
        return KakaoPayReadyRequest.builder()
                .cid(cid)
                .partnerOrderId(subsOrderNo)
                .partnerUserId(memberId.toString())
                .itemName(name)
                .quantity(1L)
                .totalAmount(price)
                .taxFreeAmount(0L)
                .approvalUrl(approvalUrl)
                .cancelUrl(cancelUrl)
                .failUrl(failUrl)
                .build();
    }
}
