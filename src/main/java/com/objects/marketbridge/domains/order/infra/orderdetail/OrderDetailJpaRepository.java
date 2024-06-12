package com.objects.marketbridge.domains.order.infra.orderdetail;

import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.product.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update OrderDetail pod set pod.statusCode = :type where pod.order.id = :orderId")
    int changeAllType(@Param("orderId") Long orderId, @Param("type") String type);

    List<OrderDetail> findByProductId(Long memberId);

    List<OrderDetail> findByOrderNo(String orderNo);

    List<OrderDetail> findByOrder_IdAndProductIn(Long orderId, List<Product> products);

    List<OrderDetail> findByOrderNoAndProduct_IdIn(String orderNo, List<Long> productIds);

    List<OrderDetail> findByOrderNoAndIdIn(String orderNo, List<Long> orderDetailIds);

    @Query(value = "SELECT o.member_id, od.* " +
            "FROM order_detail od " +
            "INNER JOIN orders o ON od.order_id = o.order_id " +
            "WHERE o.member_id = :memberId", nativeQuery = true)
    Page<OrderDetail> findAllByMemberId(Long memberId, Pageable pageable);

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OrderDetail> findById(Long id);
}
