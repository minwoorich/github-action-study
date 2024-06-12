package com.objects.marketbridge.domains.payment.infra;

import com.objects.marketbridge.domains.payment.domain.Payment;
import com.objects.marketbridge.domains.payment.service.port.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public void save(Payment payment) {
        paymentJpaRepository.save(payment);
    }

    @Override
    public Payment findById(Long id) {
        return paymentJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("엔티티가 존재하지 않습니다."));
    }

    @Override
    public Payment findByTid(String tid) {
        return paymentJpaRepository.findByTid(tid).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Payment findByOrderId(Long orderId) {
        return paymentJpaRepository.findByOrderId(orderId).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Payment findByOrderNo(String orderNo) {
        return paymentJpaRepository.findByOrderNo(orderNo).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public void deleteAllInBatch() {
        paymentJpaRepository.deleteAllInBatch();
    }
}
