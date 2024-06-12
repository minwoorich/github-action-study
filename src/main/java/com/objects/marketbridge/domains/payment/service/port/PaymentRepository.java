package com.objects.marketbridge.domains.payment.service.port;


import com.objects.marketbridge.domains.payment.domain.Payment;

public interface PaymentRepository {
    void save(Payment payment);
    Payment findById(Long id);

    Payment findByTid(String tid);

    Payment findByOrderId(Long orderId);

    Payment findByOrderNo(String orderNo);

    void deleteAllInBatch();
}
