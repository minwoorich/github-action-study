package com.objects.marketbridge.common.kakao.enums;


import lombok.Getter;

@Getter
public enum KakaoStatus {
    READY,
    SEND_TMS,
    OPEN_PAYMENT,
    SELECT_METHOD,
    SUCCESS_PAYMENT,
    ISSUED_SID,
    PART_CANCEL_PAYMENT,
    CANCEL_PAYMENT,
    QUIT_PAYMENT,
    FAIL_PAYMENT;
}
