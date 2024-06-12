package com.objects.marketbridge.domains.review.infra;

import com.objects.marketbridge.domains.review.domain.ReviewLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewLikeJpaRepository extends JpaRepository<ReviewLike, Long> {
    Boolean existsByReviewIdAndMemberId(Long reviewId, Long memberId);

    List<ReviewLike> findAllByReviewId(Long reviewId);

    ReviewLike findByReviewIdAndMemberId(Long reviewId, Long memberId);

    Page<ReviewLike> findAllByReview_Product_Id(Long productId, Pageable pageable);

    Page<ReviewLike> findAllByReview_Member_Id(Long memberId, Pageable pageable);

    void delete(ReviewLike reviewLike);

    void deleteById(Long reviewLikeId);

    void deleteAllByReviewId(Long reviewId);

    void deleteByReviewIdAndMemberId(Long reviewId, Long memberId);

    Long countByReviewId(Long reviewId);

    void deleteAllByIdIn(List<Long> reviewLikeIds);
}
