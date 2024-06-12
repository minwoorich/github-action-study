package com.objects.marketbridge.domains.review.infra;

import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.domain.StatusCode;
import com.objects.marketbridge.domains.review.service.port.OrderDetailReviewRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@Transactional(readOnly = true)
public class OrderDetailReviewRepositoryImpl implements OrderDetailReviewRepository{

    private final OrderDetailReviewJpaRepository orderDetailReviewJpaRepository;

    @Builder
    public OrderDetailReviewRepositoryImpl(OrderDetailReviewJpaRepository orderDetailReviewJpaRepository) {
        this.orderDetailReviewJpaRepository = orderDetailReviewJpaRepository;
    }

    public Page<OrderDetail> findAllByMemberId(Long memberId, Pageable pageable){
        return orderDetailReviewJpaRepository.findAllByMemberId(memberId, pageable);
    }

    @Override
    public Page<OrderDetail> findAllByMemberIdAndStatusCode(Long memberId, String statusCode, Pageable pageable) {
        return orderDetailReviewJpaRepository.findAllByMemberIdAndStatusCode(memberId, statusCode, pageable);
    }

    @Override
    public Long countByMemberIdAndStatusCode(Long memberId, String statusCode) {
        return orderDetailReviewJpaRepository.countByMemberIdAndStatusCode(memberId, statusCode);
    }
}
