package com.objects.marketbridge.domains.payment.infra;

import com.objects.marketbridge.common.kakao.dto.KakaoPayApproveRequest;
import com.objects.marketbridge.common.kakao.KakaoPayService;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.payment.service.dto.PaymentDto;
import com.objects.marketbridge.domains.payment.service.dto.RefundCancelDto;
import com.objects.marketbridge.domains.payment.service.dto.RefundDto;
import com.objects.marketbridge.domains.payment.service.port.PaymentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.objects.marketbridge.common.kakao.KakaoPayConfig.ONE_TIME_CID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentClientImpl implements PaymentClient {

    private final KakaoPayService kakaoPayService;

    @Override
    public RefundDto refund(RefundCancelDto refundCancelDto) {
        return RefundDto.of(kakaoPayService.cancel(refundCancelDto));
    }

    @Override
    public PaymentDto payment(Order order, String pgToken) {
        return PaymentDto.of(kakaoPayService.approve(createKakaoRequest(order, pgToken)));
    }

    private KakaoPayApproveRequest createKakaoRequest(Order order, String pgToken) {
        return KakaoPayApproveRequest.builder()
                .pgToken(pgToken)
                .partnerUserId(order.getMember().getId().toString())
                .partnerOrderId(order.getOrderNo())
                .tid(order.getTid())
                .totalAmount(order.getTotalPrice())
                .cid(ONE_TIME_CID)
                .build();
    }
}
