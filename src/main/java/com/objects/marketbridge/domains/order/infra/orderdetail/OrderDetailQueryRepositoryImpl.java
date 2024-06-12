package com.objects.marketbridge.domains.order.infra.orderdetail;

import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.service.port.OrderDetailQueryRepository;
import com.objects.marketbridge.domains.product.domain.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class OrderDetailQueryRepositoryImpl implements OrderDetailQueryRepository {

    private final OrderDetailJpaRepository orderDetailJpaRepository;
    private final JPAQueryFactory queryFactory;

    public OrderDetailQueryRepositoryImpl(OrderDetailJpaRepository orderDetailJpaRepository, EntityManager em) {
        this.orderDetailJpaRepository = orderDetailJpaRepository;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public OrderDetail findById(Long id) {
        return orderDetailJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("엔티티가 존재하지 않습니다"));
    }

    @Override
    public List<OrderDetail> findAll() {
        return orderDetailJpaRepository.findAll();
    }

    @Override
    public List<OrderDetail> findByOrderNo(String orderNo) {
        return orderDetailJpaRepository.findByOrderNo(orderNo);
    }

    @Override
    public List<OrderDetail> findByOrder_IdAndProductIn(Long orderId, List<Product> products) {
        return orderDetailJpaRepository.findByOrder_IdAndProductIn(orderId, products);
    }

    @Override
    public List<OrderDetail> findByOrderNoAndProduct_IdIn(String orderNo, List<Long> productIds) {
        return orderDetailJpaRepository.findByOrderNoAndProduct_IdIn(orderNo, productIds);
    }

}
