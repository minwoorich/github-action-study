package com.objects.marketbridge.domains.order.service.port;


import com.objects.marketbridge.domains.order.domain.Order;

import java.util.List;

public interface OrderCommandRepository {

    Order save(Order order);

    void saveAll(List<Order> orders);

    void deleteAllInBatch();

    void deleteByOrderNo(String orderNo);
}
