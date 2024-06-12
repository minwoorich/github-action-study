package com.objects.marketbridge.domains.order.mock;

import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.service.port.OrderCommandRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Objects;

public class FakeOrderCommandRepository extends BaseFakeOrderRepository implements OrderCommandRepository {

    @Override
    public Order save(Order order) {
        if (order.getId() == null || order.getId() == 0) {
            ReflectionTestUtils.setField(order, "id", increaseId(), Long.class);
        } else {
            getInstance().getData().removeIf(item -> Objects.equals(item.getId(), order.getId()));
        }
        getInstance().getData().add(order);
        return order;
    }

    @Override
    public void saveAll(List<Order> orders) {
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("주어진 주문 리스트가 존재하지 않습니다.");
        }
        orders.forEach(this::save);
    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public void deleteByOrderNo(String orderNo) {

    }
}
