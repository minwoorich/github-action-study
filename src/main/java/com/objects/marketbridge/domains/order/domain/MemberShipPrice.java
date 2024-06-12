package com.objects.marketbridge.domains.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberShipPrice {

    BASIC(3000L, 1000L, 1000L),
    WOW(0L, 0L, 0L);

    private final Long deliveryFee;
    private final Long refundFee;
    private final Long returnFee;
}
