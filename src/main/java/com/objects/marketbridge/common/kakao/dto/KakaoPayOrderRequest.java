package com.objects.marketbridge.common.kakao.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoPayOrderRequest {
    private String cid;
    private String tid;

    @Builder
    public KakaoPayOrderRequest(String cid, String tid) {
        this.cid = cid;
        this.tid = tid;
    }

    public static KakaoPayOrderRequest create(String cid, String tid) {
        return KakaoPayOrderRequest.builder()
                .cid(cid)
                .tid(tid)
                .build();
    }
}
