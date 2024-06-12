package com.objects.marketbridge.domains.order.service;


import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.domain.StatusCodeType;
import com.objects.marketbridge.domains.payment.service.port.PaymentClient;
import com.objects.marketbridge.domains.order.service.dto.ConfirmCancelDto;
import com.objects.marketbridge.domains.order.service.dto.GetCancelDetailDto;
import com.objects.marketbridge.domains.order.service.dto.RequestCancelDto;
import com.objects.marketbridge.domains.order.service.port.OrderCancelReturnCommandRepository;
import com.objects.marketbridge.domains.order.service.port.OrderCancelReturnQueryRepository;
import com.objects.marketbridge.domains.order.service.port.OrderDetailCommandRepository;
import com.objects.marketbridge.domains.order.service.port.OrderDetailQueryRepository;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.Builder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.NON_CANCELLABLE_PRODUCT;


@Service
@Timed("order")
@Transactional(readOnly = true)
public class OrderCancelService extends OrderCancelReturnService {

    @Builder
    public OrderCancelService(PaymentClient paymentClient, DateTimeHolder dateTimeHolder, OrderDetailQueryRepository orderDetailQueryRepository, OrderDetailCommandRepository orderDetailCommandRepository, OrderCancelReturnQueryRepository orderCancelReturnQueryRepository, OrderCancelReturnCommandRepository orderCancelReturnCommandRepository) {
        super(paymentClient, dateTimeHolder, orderDetailQueryRepository, orderDetailCommandRepository, orderCancelReturnQueryRepository, orderCancelReturnCommandRepository);
    }

    @Counted("order")
    @Transactional
    public ConfirmCancelDto.Response confirmCancel(ConfirmCancelDto.Request request, DateTimeHolder dateTimeHolder) {
        return confirmProcess(
                request.getOrderDetailId(),
                request.getReason(),
                request.getNumberOfCancellation(),
                dateTimeHolder,
                OrderDetail::cancel,
                ConfirmCancelDto.Response::of
        );
    }

    public RequestCancelDto.Response findCancelInfo(Long orderDetailId, Long numberOfCancellation, String membership) {
        // TODO fetchJoin으로 변경 (Product 까지)
        OrderDetail orderDetail = valifyCancelOrderDetail(orderDetailId);

        return RequestCancelDto.Response.of(orderDetail, numberOfCancellation, membership);
    }

    public GetCancelDetailDto.Response findCancelDetail(Long cancelledOrderDetailId, String membership) {
        OrderCancelReturn cancelDetail = orderCancelReturnQueryRepository.findById(cancelledOrderDetailId);

        return GetCancelDetailDto.Response.of(cancelDetail, membership, dateTimeHolder);
    }

    private OrderDetail valifyCancelOrderDetail(Long orderDetailId) {
        OrderDetail orderDetail = orderDetailQueryRepository.findById(orderDetailId);
        if(cancelImpossible(orderDetail)) {
            throw CustomLogicException.createBadRequestError(NON_CANCELLABLE_PRODUCT);
        }
        return orderDetail;
    }

    private boolean cancelImpossible(OrderDetail orderDetail) {
        return Objects.equals(orderDetail.getStatusCode(), StatusCodeType.DELIVERY_COMPLETED.getCode());
    }

}
