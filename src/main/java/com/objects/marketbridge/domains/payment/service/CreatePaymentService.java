package com.objects.marketbridge.domains.payment.service;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import com.objects.marketbridge.common.kakao.KakaoPayConfig;
import com.objects.marketbridge.common.kakao.KakaoPayService;
import com.objects.marketbridge.common.kakao.dto.KakaoPayApproveRequest;
import com.objects.marketbridge.common.kakao.dto.KakaoPayApproveResponse;
import com.objects.marketbridge.common.kakao.dto.KakaoPayOrderRequest;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.service.port.OrderQueryRepository;
import com.objects.marketbridge.domains.payment.controller.dto.CompleteOrderHttp;
import com.objects.marketbridge.domains.payment.domain.Amount;
import com.objects.marketbridge.domains.payment.domain.CardInfo;
import com.objects.marketbridge.domains.payment.domain.Payment;
import com.objects.marketbridge.domains.payment.service.port.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

import static com.objects.marketbridge.domains.order.domain.StatusCodeType.PAYMENT_COMPLETED;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final KakaoPayService kakaoPayService;


    @Transactional
    public CompleteOrderHttp.Response create(KakaoPayApproveResponse response) {

        // 1. Payment 엔티티 생성
        Payment payment = createPayment(response);
        paymentRepository.save(payment);

        // 2. Order - Payment 연관관계 매핑
        Order order = orderQueryRepository.findByOrderNoWithOrderDetailsAndProduct(response.getPartnerOrderId());
        order.linkPayment(payment);

        // 3. orderDetail 의 statusCode 업데이트
        payment.changeStatusCode(PAYMENT_COMPLETED.getCode());

        return CompleteOrderHttp.Response.of(payment);
    }

    private Payment createPayment(KakaoPayApproveResponse response) {

        String orderNo = response.getPartnerOrderId();
        String paymentMethod = response.getPaymentMethodType();
        String tid = response.getTid();
        CardInfo cardInfo = response.getCardInfo();
        Amount amount = response.getAmount();
        LocalDateTime approvedAt = response.getApprovedAt();

        return Payment.create(orderNo, paymentMethod, tid, cardInfo, amount, approvedAt);
    }

    public KakaoPayApproveResponse approve(String orderNo, String pgToken) throws RestClientException {
        Order order = orderQueryRepository.findByOrderNoWithMember(orderNo);
        validAmount(KakaoPayOrderRequest.create(KakaoPayConfig.ONE_TIME_CID, order.getTid()), order.getRealPrice());

        return kakaoPayService.approve(KakaoPayApproveRequest.create(order, pgToken));
    }

    private void validAmount(KakaoPayOrderRequest request, Long savedAmount) {
        Long totalAmount = kakaoPayService.getOrders(request).getAmount().getTotalAmount();
        if (!totalAmount.equals(savedAmount)) {
            throw CustomLogicException.createBadRequestError(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
    }
}
