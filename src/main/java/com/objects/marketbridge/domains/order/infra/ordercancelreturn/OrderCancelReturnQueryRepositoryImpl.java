package com.objects.marketbridge.domains.order.infra.ordercancelreturn;

import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;
import com.objects.marketbridge.domains.order.service.port.OrderCancelReturnQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
public class OrderCancelReturnQueryRepositoryImpl implements OrderCancelReturnQueryRepository {

    private final OrderCancelReturnJpaRepository orderCancelReturnJpaRepository;
    private final JPAQueryFactory queryFactory;

    public OrderCancelReturnQueryRepositoryImpl(OrderCancelReturnJpaRepository orderCancelReturnJpaRepository, EntityManager em) {
        this.orderCancelReturnJpaRepository = orderCancelReturnJpaRepository;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public OrderCancelReturn findById(Long id) {
        return orderCancelReturnJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("엔티티가 존재하지 않습니다"));
    }
}
