package com.objects.marketbridge.domains.order.infra.ordercancelreturn;

import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;
import com.objects.marketbridge.domains.order.service.port.OrderCancelReturnCommandRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class OrderCancelReturnCommandRepositoryImpl implements OrderCancelReturnCommandRepository {

    private final OrderCancelReturnJpaRepository orderCancelReturnJpaRepository;
    private final JPAQueryFactory queryFactory;

    public OrderCancelReturnCommandRepositoryImpl(OrderCancelReturnJpaRepository orderCancelReturnJpaRepository, EntityManager em) {
        this.orderCancelReturnJpaRepository = orderCancelReturnJpaRepository;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public OrderCancelReturn save(OrderCancelReturn orderCancelReturn) {
        return orderCancelReturnJpaRepository.save(orderCancelReturn);
    }
}
