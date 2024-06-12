package com.objects.marketbridge.domains.review.infra;

import com.objects.marketbridge.domains.review.domain.ReviewLike;
import com.objects.marketbridge.domains.review.service.port.ReviewLikeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewLikeRepositoryImpl implements ReviewLikeRepository {

    private final EntityManager em;
    private final ReviewLikeJpaRepository reviewLikeJpaRepository;

    @Override
    public void save(ReviewLike reviewLike) {
        reviewLikeJpaRepository.save(reviewLike);
    }

    @Override
    public Boolean existsByReviewIdAndMemberId(Long reviewId, Long memberId) {
        return reviewLikeJpaRepository.existsByReviewIdAndMemberId(reviewId, memberId);
    }

    @Override
    public ReviewLike findByReviewIdAndMemberId(Long reviewId, Long memberId) {
        return reviewLikeJpaRepository.findByReviewIdAndMemberId(reviewId, memberId);

    }

    @Override
    public Page<ReviewLike> findAllByReview_Product_Id(Long productId, Pageable pageable) {
        return reviewLikeJpaRepository.findAllByReview_Product_Id(productId, pageable);
    }

    @Override
    public Page<ReviewLike> findAllByReview_Member_Id(Long memberId, Pageable pageable) {
        return reviewLikeJpaRepository.findAllByReview_Member_Id(memberId, pageable);
    }

    @Override
    public List<Long> findReviewIdsByProductIdOrderByLikedCount(Long productId) {
        String sql = "SELECT r.review_id " +
                "FROM review_likes rl " +
                "JOIN review r ON rl.review_id = r.review_id " +
                "WHERE r.product_id = :productId " +
                "GROUP BY r.review_id " +
                "ORDER BY COUNT(rl.review_id) DESC";
        return em.createNativeQuery(sql)
                .setParameter("productId", productId)
                .getResultList();
    }

    @Override
    public void delete(ReviewLike reviewLike) {
        reviewLikeJpaRepository.delete(reviewLike);
    }

    @Override
    public List<ReviewLike> findAllByReviewId(Long reviewId) {
        return reviewLikeJpaRepository.findAllByReviewId(reviewId);
    }

    @Override
    public void deleteAllByReviewId(Long reviewId) {
        reviewLikeJpaRepository.deleteAllByReviewId(reviewId);
    }

    @Override
    public void deleteByReviewIdAndMemberId(Long reviewId, Long memberId) {
        reviewLikeJpaRepository.deleteByReviewIdAndMemberId(reviewId, memberId);
    }

    @Override
    public Long countByReviewId(Long reviewId) {
        return reviewLikeJpaRepository.countByReviewId(reviewId);
    }

    @Override
    public void deleteAllByIdIn(List<Long> reviewLikeIds) {
        reviewLikeJpaRepository.deleteAllByIdIn(reviewLikeIds);
    }
}
