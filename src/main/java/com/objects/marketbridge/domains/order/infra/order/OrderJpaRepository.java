package com.objects.marketbridge.domains.order.infra.order;

import com.objects.marketbridge.domains.order.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "orderDetails.product")
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails od LEFT JOIN FETCH od.product WHERE o.orderNo = :orderNo")
    Optional<Order> findByOrderNoWithOrderDetailsAndProduct(@Param("orderNo") String orderNo);

    Optional<Order> findByOrderNo(String orderNo);

    @Query("select o from Order o join fetch o.member where o.orderNo = :orderNo")
    Optional<Order> findByOrderNoWithMember(String orderNo);

    void deleteByOrderNo(String orderNo);

}
