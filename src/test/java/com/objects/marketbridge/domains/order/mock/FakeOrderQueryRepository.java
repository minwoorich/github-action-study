package com.objects.marketbridge.domains.order.mock;

import com.objects.marketbridge.domains.order.controller.dto.select.GetOrderHttp;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.service.port.OrderQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class FakeOrderQueryRepository extends BaseFakeOrderRepository implements OrderQueryRepository {

    @Override
    public Optional<Order> findById(Long orderId) {
        return getInstance().getData().stream()
                .filter(order -> order.getId().equals(orderId))
                .findAny();
    }

    @Override
    public Order findByOrderNo(String orderNo) {
            return getInstance().getData().stream()
                    .filter(order -> order.getOrderNo().equals(orderNo))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("orderNo와 일치하는 주문이 존재하지 않습니다."));
    }

    @Override
    public Order findByOrderNoWithMember(String orderNo) {
        return null;
    }

    @Override
    public Order findByOrderNoWithOrderDetailsAndProduct(String orderNo) {
        return null;
    }

    @Override
    public Page<Order> findAllPaged(GetOrderHttp.Condition condition, Pageable pageable) {
        return null;
    }

    @Override
    public Order findByOrderIdFetchJoin(Long orderId) {
        return null;
    }
}
