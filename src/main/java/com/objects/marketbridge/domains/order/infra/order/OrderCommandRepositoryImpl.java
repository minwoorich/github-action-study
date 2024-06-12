package com.objects.marketbridge.domains.order.infra.order;

import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.service.port.OrderCommandRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderCommandRepositoryImpl implements OrderCommandRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final JPAQueryFactory queryFactory;

    public OrderCommandRepositoryImpl(OrderJpaRepository orderJpaRepository, EntityManager em) {
        this.orderJpaRepository = orderJpaRepository;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public void deleteAllInBatch() {
        orderJpaRepository.deleteAllInBatch();
    }

    @Override
    public void saveAll(List<Order> orders) {
        orderJpaRepository.saveAll(orders);
    }

    @Override
    public void deleteByOrderNo(String orderNo) {
        orderJpaRepository.deleteByOrderNo(orderNo);
    }

}
