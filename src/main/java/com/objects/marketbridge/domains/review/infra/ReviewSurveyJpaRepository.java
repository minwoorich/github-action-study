package com.objects.marketbridge.domains.review.infra;

import com.objects.marketbridge.domains.review.domain.ReviewSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewSurveyJpaRepository extends JpaRepository<ReviewSurvey, Long> {

    ReviewSurvey save(ReviewSurvey reviewSurvey);

    void deleteAllByReviewId(Long reviewId);

    List<ReviewSurvey> findAllByReviewId(Long reviewId);

//    @Modifying
//    @Query("DELETE FROM ReviewSurvey rs WHERE rs.reviewId = :reviewId")
//    void deleteByReviewId(@Param("reviewId") Long reviewId);
}
