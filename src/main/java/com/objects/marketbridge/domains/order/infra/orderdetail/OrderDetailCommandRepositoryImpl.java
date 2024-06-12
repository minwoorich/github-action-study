package com.objects.marketbridge.domains.order.infra.orderdetail;

import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.service.port.OrderDetailCommandRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDetailCommandRepositoryImpl implements OrderDetailCommandRepository {

    private final OrderDetailJpaRepository orderDetailJpaRepository;
    private final JPAQueryFactory queryFactory;

    public OrderDetailCommandRepositoryImpl(OrderDetailJpaRepository orderDetailJpaRepository, EntityManager em) {
        this.orderDetailJpaRepository = orderDetailJpaRepository;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public int changeAllType(Long orderId, String type) {
        return orderDetailJpaRepository.changeAllType(orderId, type);
    }

    @Override
    public void saveAll(List<OrderDetail> orderDetail) {
        orderDetailJpaRepository.saveAll(orderDetail);
    }

    @Override
    public void save(OrderDetail orderDetail) {
        orderDetailJpaRepository.save(orderDetail);
    }

    @Override
    public void deleteAllInBatch() {
        orderDetailJpaRepository.deleteAllInBatch();
    }

}
