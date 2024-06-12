package com.objects.marketbridge.domains.review.service.port;

import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.domain.StatusCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderDetailReviewRepository {
    Page<OrderDetail> findAllByMemberId(Long memberId, Pageable pageable);

    Page<OrderDetail> findAllByMemberIdAndStatusCode(Long memberId, String statusCode, Pageable pageable);

    Long countByMemberIdAndStatusCode(Long memberId, String statusCode);
}
