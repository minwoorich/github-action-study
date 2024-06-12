package com.objects.marketbridge.domains.payment.service;

import com.objects.marketbridge.common.kakao.KakaoPayConfig;
import com.objects.marketbridge.common.kakao.dto.KakaoPayOrderRequest;
import com.objects.marketbridge.common.kakao.dto.KakaoPayOrderResponse;
import com.objects.marketbridge.common.kakao.KakaoPayService;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.service.port.OrderCommandRepository;
import com.objects.marketbridge.domains.order.service.port.OrderQueryRepository;
import com.objects.marketbridge.domains.payment.controller.dto.CancelledPaymentHttp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuitPaymentService {

    private final OrderCommandRepository orderCommandRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final DateTimeHolder dateTimeHolder;
    private final KakaoPayService kakaoPayService;


    @Transactional
    public void cancel(String orderNo) {
        Order order = orderQueryRepository.findByOrderNo(orderNo);

        // 1) 사용했던 쿠폰들 다시 되돌리기
        order.changeMemberCouponInfo(null);

        // 2) 감소했던 재고 다시 되돌리기
        order.stockIncrease();

        // 3) 주문, 상세주문들 softDelete
        orderCommandRepository.deleteByOrderNo(orderNo);
    }

    public CancelledPaymentHttp.Response response(String orderNo) throws RestClientException {

        Order order = orderQueryRepository.findByOrderNo(orderNo);

        KakaoPayOrderResponse kakaoResponse = kakaoPayService.getOrders(KakaoPayOrderRequest.create(KakaoPayConfig.ONE_TIME_CID, order.getTid()));

        return CancelledPaymentHttp.Response.of(kakaoResponse, order);
    }
}
