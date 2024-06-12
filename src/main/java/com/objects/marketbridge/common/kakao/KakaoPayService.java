package com.objects.marketbridge.common.kakao;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.kakao.dto.*;
import com.objects.marketbridge.domains.payment.service.dto.RefundCancelDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.EXTERNAL_API_FAILURE;
import static com.objects.marketbridge.common.kakao.KakaoPayConfig.*;
import static com.objects.marketbridge.common.security.constants.SecurityConst.AUTHORIZATION;
import static io.jsonwebtoken.Header.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private final KakaoPayConfig kakaoPayConfig;

    public KakaoPayReadyResponse ready(KakaoPayReadyRequest request) {

        RestClient restClient = setup();

        return restClient.post()
                .uri(READY_END_POINT)
                .body(request)
                .retrieve()
                .body(KakaoPayReadyResponse.class);
    }


    //정기구독 1회차 , 단건결제
    public KakaoPayApproveResponse approve(KakaoPayApproveRequest request) throws CustomLogicException{

        try {
            RestClient restClient = setup();

            return restClient.post()
                    .uri(APPROVE_END_POINT)
                    .body(request)
                    .retrieve()
                    .body(KakaoPayApproveResponse.class);

        } catch (Exception e) {
            throw CustomLogicException.builderWithCause()
                    .cause(e)
                    .message(e.getMessage())
                    .errorCode(EXTERNAL_API_FAILURE)
                    .timestamp(LocalDateTime.now())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    //정기구독 2회차
    public KakaoPayApproveResponse subsApprove(KakaoPaySubsApproveRequest request) {

        RestClient restClient = setup();

        return restClient.post()
                .uri(SUBS_END_POINT)
                .body(request)
                .retrieve()
                .body(KakaoPayApproveResponse.class);
    }

    // 취소
    public KaKaoPayCancelResponse cancel(RefundCancelDto refundCancelDto) {
        KakaoPayCancelRequest request = KakaoPayCancelRequest.builder()
                .cid(ONE_TIME_CID)
                .tid(refundCancelDto.getTid())
                .cancelAmount(refundCancelDto.getCancelAmount())
                .cancelTaxFreeAmount(0)
                .build();

        RestClient restClient = setup();

        return restClient.post()
                .uri(CANCEL_END_POINT)
                .body(request)
                .retrieve()
                .body(KaKaoPayCancelResponse.class);
    }

    // 정기구독 비활성화

    // 정기결제 상태조회

    // 주문조회
    public KakaoPayOrderResponse getOrders(KakaoPayOrderRequest request){

        try {
            RestClient restClient = setup();

            return restClient.post()
                    .uri(ORDER_END_POINT)
                    .body(request)
                    .retrieve()
                    .body(KakaoPayOrderResponse.class);

        } catch (Exception e) {
            throw CustomLogicException.builderWithCause()
                    .cause(e)
                    .message(e.getMessage())
                    .errorCode(EXTERNAL_API_FAILURE)
                    .timestamp(LocalDateTime.now())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private RestClient setup() {

        return RestClient.builder()
                .baseUrl(KAKAO_BASE_URL)
                .defaultHeaders((httpHeaders -> {
                    httpHeaders.add(AUTHORIZATION, AUTH_SCHEME+ kakaoPayConfig.getSecretKeyDev());
                    httpHeaders.add(ACCEPT, APPLICATION_JSON+";charset=UTF-8");
                    httpHeaders.add(CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
                }))
                .build();
    }
}