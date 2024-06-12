package com.objects.marketbridge.common.kakao.enums;

import lombok.Getter;

@Getter
public enum KakaoPaymentActionType {
    PAYMENT, // 결제
    CANCEL, // 결제취소
    ISSUED_SId; // SID 발급
}
