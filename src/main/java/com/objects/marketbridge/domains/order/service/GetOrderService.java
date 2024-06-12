package com.objects.marketbridge.domains.order.service;

import com.objects.marketbridge.common.responseobj.PageResponse;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.controller.dto.select.GetOrderDetailHttp;
import com.objects.marketbridge.domains.order.controller.dto.select.GetOrderHttp;
import com.objects.marketbridge.domains.order.service.dto.GetOrderDto;
import com.objects.marketbridge.domains.order.service.port.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.objects.marketbridge.domains.order.controller.dto.select.GetOrderHttp.Condition;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetOrderService {

    private final OrderQueryRepository orderQueryRepository;

    public PageResponse<GetOrderHttp.Response> search(Pageable pageable, Condition condition) {

        Page<Order> pagedOrders = orderQueryRepository.findAllPaged(condition, pageable);

        List<GetOrderHttp.Response> response = pagedOrders.stream()
                .map(GetOrderDto::of)           // Order -> GetOrderDto
                .map(GetOrderHttp.Response::of) // GetOrderDto -> GetOrderHttp.Response
                .toList();

        return new PageResponse<>(new PageImpl<>(response, pageable, pagedOrders.getTotalElements()));
    }

    public GetOrderDetailHttp.Response getOrderDetails(Long orderId) {
        Order order = orderQueryRepository.findByOrderIdFetchJoin(orderId);

        return GetOrderDetailHttp.Response.of(GetOrderDto.of(order));
    }
}
