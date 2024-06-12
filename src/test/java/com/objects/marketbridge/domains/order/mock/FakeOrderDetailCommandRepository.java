package com.objects.marketbridge.domains.order.mock;

import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.service.port.OrderDetailCommandRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Objects;

public class FakeOrderDetailCommandRepository extends BaseFakeOrderDetailRepository implements OrderDetailCommandRepository {

    @Override
    public void save(OrderDetail orderDetail) {
        if (orderDetail.getId() == null || orderDetail.getId() == 0) {
            ReflectionTestUtils.setField(orderDetail, "id", getInstance().increaseId(), Long.class);
            getInstance().getData().add(orderDetail);
        } else {
            getInstance().getData().removeIf(item -> Objects.equals(item.getId(), orderDetail.getId()));
            getInstance().getData().add(orderDetail);
        }
    }

    @Override
    public void saveAll(List<OrderDetail> orderDetails) {
        orderDetails.forEach(this::save);
    }

    @Override
    public int changeAllType(Long orderId, String type) {
        return 0;
    }

    @Override
    public void deleteAllInBatch() {

    }
}
