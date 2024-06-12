package com.objects.marketbridge.domains.payment.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefundCancelDto {

    String tid;
    Integer cancelAmount;

    @Builder
    public RefundCancelDto(String tid, Integer cancelAmount) {
        this.tid = tid;
        this.cancelAmount = cancelAmount;
    }
}
