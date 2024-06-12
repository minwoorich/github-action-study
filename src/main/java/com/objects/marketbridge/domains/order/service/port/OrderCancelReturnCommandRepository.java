package com.objects.marketbridge.domains.order.service.port;

import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;

public interface OrderCancelReturnCommandRepository {
    OrderCancelReturn save(OrderCancelReturn orderCancelReturn);
}
