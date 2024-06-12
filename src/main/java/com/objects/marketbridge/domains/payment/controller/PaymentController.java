package com.objects.marketbridge.domains.payment.controller;

import com.objects.marketbridge.common.exception.advice.ErrorResult;
import com.objects.marketbridge.common.kakao.dto.KakaoPayApproveResponse;
import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.common.utils.ErrorLoggerUtils;
import com.objects.marketbridge.domains.payment.controller.dto.CancelledPaymentHttp;
import com.objects.marketbridge.domains.payment.controller.dto.CompleteOrderHttp;
import com.objects.marketbridge.domains.payment.service.CreatePaymentService;
import com.objects.marketbridge.domains.payment.service.QuitPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;

import java.time.LocalDateTime;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.NO_ERROR_CODE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final static String NONE = "none";

    private final CreatePaymentService createPaymentService;
    private final QuitPaymentService quitPaymentService;

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResult.Response kakaoApiExHandler(Exception e, HttpServletRequest httpRequest, HandlerMethod handlerMethod) {

        StringBuilder sb = new StringBuilder();

        // 사용쿠폰, 재고, 주문, 상세주문 전부 롤백
        quitPaymentService.cancel(getOrderNoFromUri(httpRequest.getRequestURI()));

        ErrorResult errorResult = ErrorResult.builder()
                .code(NOT_FOUND.value())
                .status(NOT_FOUND)
                .path(String.valueOf(sb.append(httpRequest.getMethod()).append(httpRequest.getRequestURI())))
                .errorCode(NO_ERROR_CODE)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .className(handlerMethod.getBeanType().getName())
                .methodName(handlerMethod.getMethod().getName())
                .exceptionName(e.getClass().getName())
                .build();

        ErrorLoggerUtils.errorLog(errorResult);

        return errorResult.toResponse();
    }

    @GetMapping("/payment/kakao-pay/approval/{orderNo}") // 결제 승인 성공
    public ApiResponse<CompleteOrderHttp.Response> createPayment(
            @RequestParam(name = "pg_token") String pgToken,
            @PathVariable(name = "orderNo") String orderNo) {

        // 1. kakaoPayService 로 결제 승인 요청 보내기
        KakaoPayApproveResponse kakaoResponse = createPaymentService.approve(orderNo, pgToken);

        // 2. Payment 생성 및 OrderDetails 업데이트
        CompleteOrderHttp.Response response = createPaymentService.create(kakaoResponse);

        return ApiResponse.ok(response);
    }

    @GetMapping("/kakao-pay/fail/{orderNo}") // 결제 승인 실패 (취소랑 동일함)
    public ApiResponse<CancelledPaymentHttp.Response> kakaoPaymentApproveFail(
            @PathVariable(name = "orderNo") String orderNo){

        CancelledPaymentHttp.Response response = quitPaymentService.response(orderNo);
        quitPaymentService.cancel(orderNo);

        return ApiResponse.ok(response);
    }

    @GetMapping("/kakao-pay/cancel/{orderNo}") // 결제 승인 취소
    public ApiResponse<CancelledPaymentHttp.Response> kakaoPaymentApproveCancel(
            @PathVariable(name = "orderNo") String orderNo){

        CancelledPaymentHttp.Response response = quitPaymentService.response(orderNo);
        quitPaymentService.cancel(orderNo);

        return ApiResponse.ok(response);
    }

    private String getOrderNoFromUri(String requestUri) {
        String[] uri = requestUri.split("/");
        return uri[uri.length - 1];
    }
}
