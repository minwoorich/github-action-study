package com.objects.marketbridge.domains.order.service.port;

import com.objects.marketbridge.domains.order.domain.OrderDetail;

import java.util.List;

public interface OrderDetailCommandRepository {

    void save(OrderDetail orderDetail);

    void saveAll(List<OrderDetail> orderDetail);

    int changeAllType(Long orderId, String type);

    void deleteAllInBatch();

}
