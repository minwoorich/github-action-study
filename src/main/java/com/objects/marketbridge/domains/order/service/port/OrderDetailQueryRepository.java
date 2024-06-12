package com.objects.marketbridge.domains.order.service.port;

import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.product.domain.Product;

import java.util.List;

public interface OrderDetailQueryRepository {

    OrderDetail findById(Long id);

    List<OrderDetail> findAll();

    List<OrderDetail> findByOrderNo(String orderNo);

    List<OrderDetail> findByOrder_IdAndProductIn(Long orderId, List<Product> products);

    List<OrderDetail> findByOrderNoAndProduct_IdIn(String orderNo, List<Long> productIds);

}
