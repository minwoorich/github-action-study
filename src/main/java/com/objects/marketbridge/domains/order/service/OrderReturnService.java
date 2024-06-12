package com.objects.marketbridge.domains.order.service;


import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.domain.StatusCodeType;
import com.objects.marketbridge.domains.payment.service.port.PaymentClient;
import com.objects.marketbridge.domains.order.service.dto.ConfirmReturnDto;
import com.objects.marketbridge.domains.order.service.dto.GetReturnDetailDto;
import com.objects.marketbridge.domains.order.service.dto.RequestReturnDto;
import com.objects.marketbridge.domains.order.service.port.OrderCancelReturnCommandRepository;
import com.objects.marketbridge.domains.order.service.port.OrderCancelReturnQueryRepository;
import com.objects.marketbridge.domains.order.service.port.OrderDetailCommandRepository;
import com.objects.marketbridge.domains.order.service.port.OrderDetailQueryRepository;
import lombok.Builder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.NON_RETURNABLE_PRODUCT;


@Service
@Transactional(readOnly = true)
public class OrderReturnService extends OrderCancelReturnService {

    @Builder
    public OrderReturnService(PaymentClient paymentClient, DateTimeHolder dateTimeHolder, OrderDetailQueryRepository orderDetailQueryRepository, OrderDetailCommandRepository orderDetailCommandRepository, OrderCancelReturnQueryRepository orderCancelReturnQueryRepository, OrderCancelReturnCommandRepository orderCancelReturnCommandRepository) {
        super(paymentClient, dateTimeHolder, orderDetailQueryRepository, orderDetailCommandRepository, orderCancelReturnQueryRepository, orderCancelReturnCommandRepository);
    }

    @Transactional
    public ConfirmReturnDto.Response confirmReturn(ConfirmReturnDto.Request request, DateTimeHolder dateTimeHolder) {
        return confirmProcess(
                request.getOrderDetailId(),
                request.getReason(),
                request.getNumberOfReturns(),
                dateTimeHolder,
                OrderDetail::returns,
                ConfirmReturnDto.Response::of
        );
    }

    public RequestReturnDto.Response findReturnInfo(Long orderDetailId, Long numberOfReturns, String membership) {
        // TODO fetchJoin으로 변경
        OrderDetail orderDetail = valifyReturnOrderDetail(orderDetailId);

        return RequestReturnDto.Response.of(orderDetail, numberOfReturns, membership);
    }

    public GetReturnDetailDto.Response findReturnDetail(Long returnedOrderDetailId, String membership) {
        OrderCancelReturn returnDetail = orderCancelReturnQueryRepository.findById(returnedOrderDetailId);

        return GetReturnDetailDto.Response.of(returnDetail, membership, dateTimeHolder);
    }

    private OrderDetail valifyReturnOrderDetail(Long orderDetailId) {
        OrderDetail orderDetail = orderDetailQueryRepository.findById(orderDetailId);
        if(returnImpossible(orderDetail)) {
            throw CustomLogicException.createBadRequestError(NON_RETURNABLE_PRODUCT);
        }
        return orderDetail;
    }

    private boolean returnImpossible(OrderDetail orderDetail) {
        return !Objects.equals(orderDetail.getStatusCode(), StatusCodeType.DELIVERY_COMPLETED.getCode());
    }

}
