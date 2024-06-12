package com.objects.marketbridge.domains.order.mock;

import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.service.port.OrderDetailQueryRepository;
import com.objects.marketbridge.domains.product.domain.Product;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public class FakeOrderDetailQueryRepository extends BaseFakeOrderDetailRepository implements OrderDetailQueryRepository {
    @Override
    public OrderDetail findById(Long id) {
        return getInstance().getData().stream()
                .filter(od -> od.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("엔티티가 존재하지 않습니다"));
    }

    @Override
    public List<OrderDetail> findAll(){
        return null;
    }

    @Override
    public List<OrderDetail> findByOrderNo(String orderNo) {
        return null;
    }

    @Override
    public List<OrderDetail> findByOrder_IdAndProductIn(Long orderId, List<Product> products) {
        return null;
    }

    @Override
    public List<OrderDetail> findByOrderNoAndProduct_IdIn(String orderNo, List<Long> productIds) {
        return getInstance().getData().stream()
                .filter(od -> od.getOrder().getOrderNo().equals(orderNo)
                        && productIds.contains(od.getProduct().getId()))
                .toList();
    }

}
