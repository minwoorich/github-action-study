package com.objects.marketbridge.domains.order.mock;

import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;
import com.objects.marketbridge.domains.order.service.port.OrderCancelReturnQueryRepository;
import jakarta.persistence.EntityNotFoundException;

public class FakeOrderCancelReturnQueryRepository extends BaseFakeOrderCancelReturnRepository implements OrderCancelReturnQueryRepository {
    @Override
    public OrderCancelReturn findById(Long id) {
        return getInstance().getData().stream()
                .filter(ocr -> ocr.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("엔티티가 존재하지 않습니다"));
    }
}
