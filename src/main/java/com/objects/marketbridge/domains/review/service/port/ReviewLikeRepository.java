package com.objects.marketbridge.domains.review.service.port;

import com.objects.marketbridge.domains.review.domain.ReviewLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewLikeRepository {
    void save(ReviewLike reviewLike);

    Boolean existsByReviewIdAndMemberId(Long reviewId, Long memberId);

    List<ReviewLike> findAllByReviewId(Long reviewId);

    ReviewLike findByReviewIdAndMemberId(Long reviewId, Long memberId);

    Page<ReviewLike> findAllByReview_Product_Id(Long productId, Pageable pageable);

    Page<ReviewLike> findAllByReview_Member_Id(Long memberId, Pageable pageable);

    List<Long> findReviewIdsByProductIdOrderByLikedCount(Long productId);

    void delete(ReviewLike reviewLike);

    void deleteAllByReviewId(Long reviewId);

    void deleteByReviewIdAndMemberId(Long reviewId, Long memberId);

    Long countByReviewId(Long reviewId);

    void deleteAllByIdIn(List<Long> reviewLikeId);
}
