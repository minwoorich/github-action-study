package com.objects.marketbridge.domains.review.infra;

import com.objects.marketbridge.domains.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByProductId(Long productId, Pageable pageable);

    Page<Review> findAllByMemberId(Long memberId, Pageable pageable);

    Long countByProductId(Long productId);

    Long countByMemberId(Long memberId);

    Page<Review> findAllByIdIn(List<Long> likedOrderedReviewIds, Pageable pageable);
}
