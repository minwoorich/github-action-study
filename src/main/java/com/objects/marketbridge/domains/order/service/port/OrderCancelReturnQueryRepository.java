package com.objects.marketbridge.domains.order.service.port;

import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;

public interface OrderCancelReturnQueryRepository {
    OrderCancelReturn findById(Long id);
}
