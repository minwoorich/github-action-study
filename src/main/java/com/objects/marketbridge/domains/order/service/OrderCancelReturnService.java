package com.objects.marketbridge.domains.order.service;


import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.order.domain.CancelReturnStatusCode;
import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.payment.service.dto.RefundCancelDto;
import com.objects.marketbridge.domains.payment.service.dto.RefundDto;
import com.objects.marketbridge.domains.payment.service.port.PaymentClient;
import com.objects.marketbridge.domains.order.service.port.OrderCancelReturnCommandRepository;
import com.objects.marketbridge.domains.order.service.port.OrderCancelReturnQueryRepository;
import com.objects.marketbridge.domains.order.service.port.OrderDetailCommandRepository;
import com.objects.marketbridge.domains.order.service.port.OrderDetailQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public abstract class OrderCancelReturnService {

    protected final PaymentClient paymentClient;

    protected final DateTimeHolder dateTimeHolder;

    protected final OrderDetailQueryRepository orderDetailQueryRepository;
    protected final OrderDetailCommandRepository orderDetailCommandRepository;
    protected final OrderCancelReturnQueryRepository orderCancelReturnQueryRepository;
    private final OrderCancelReturnCommandRepository orderCancelReturnCommandRepository;

    protected <T> T confirmProcess(Long orderDetailId, String reason, Long numberOfOperations, DateTimeHolder dateTimeHolder, OrderDetailOperation operation, ResponseBuilder<T> responseBuilder) {
        OrderDetail orderDetail = orderDetailQueryRepository.findById(orderDetailId);

        CancelReturnStatusCode statusInfo = operation.apply(orderDetail, numberOfOperations, dateTimeHolder);

        RefundDto refundDto = paymentClient.refund(createRefundCancelDto(getTid(orderDetail), orderDetail.cancelAmount()));

        orderCancelReturnCommandRepository.save(OrderCancelReturn.create(orderDetail, statusInfo, reason));

        return responseBuilder.build(orderDetail, refundDto);
    }

    private String getTid(OrderDetail orderDetail) {
        return orderDetail.getTid();
    }

    private RefundCancelDto createRefundCancelDto(String tid, Integer cancelAmount) {
        return RefundCancelDto.builder()
                .cancelAmount(cancelAmount)
                .tid(tid)
                .build();
    }

    @FunctionalInterface
    interface OrderDetailOperation {
        CancelReturnStatusCode apply(OrderDetail orderDetail, Long numberOfOperations, DateTimeHolder dateTimeHolder);
    }

    @FunctionalInterface
    interface ResponseBuilder<T> {
        T build(OrderDetail orderDetail, RefundDto refundDto);
    }

}
