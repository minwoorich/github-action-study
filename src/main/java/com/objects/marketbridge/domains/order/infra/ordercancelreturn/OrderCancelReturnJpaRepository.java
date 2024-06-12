package com.objects.marketbridge.domains.order.infra.ordercancelreturn;

import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderCancelReturnJpaRepository extends JpaRepository<OrderCancelReturn, Long> {
}
