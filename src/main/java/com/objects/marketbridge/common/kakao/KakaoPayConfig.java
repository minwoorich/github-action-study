package com.objects.marketbridge.common.kakao;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KakaoPayConfig {

    // 카카오페이 BASE_URL
    public static final String KAKAO_BASE_URL = "https://open-api.kakaopay.com/online/v1/payment";

    // 카카오페이 API 엔드포인트
    public static final String READY_END_POINT = "/ready";
    public static final String APPROVE_END_POINT = "/approve";
    public static final String SUBS_END_POINT = "/subscription";
    public static final String CANCEL_END_POINT = "/cancel";
    public static final String ORDER_END_POINT = "/order";

    // 카카오페이 CID 및 AUTH_SCHEME
    public static final String ONE_TIME_CID = "TC0ONETIME";
    public static final String SUBS_CID = "TCSUBSCRIP";
    public static final String AUTH_SCHEME = "DEV_SECRET_KEY ";

    @Value("${payment.kakao.secret_key_dev}")
    private String secretKeyDev;

    @Value("${host}")
    private String host;

    public String getRedirectCancelUrl() {
        return host + "/kakao-pay/cancel";
    }

    public String getRedirectFailUrl() {
        return host + "/kakao-pay/fail";
    }

    public String createApprovalUrl(String uri) {
        return host + uri + "/kakao-pay/approval";
    }


}
