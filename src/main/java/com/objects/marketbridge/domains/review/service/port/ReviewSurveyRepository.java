package com.objects.marketbridge.domains.review.service.port;

import com.objects.marketbridge.domains.review.domain.ReviewSurvey;

import java.util.List;

public interface ReviewSurveyRepository {

    ReviewSurvey save(ReviewSurvey reviewSurvey);

    void deleteAllByReviewId(Long reviewId);

    List<ReviewSurvey> findAllByReviewId(Long reviewId);

//    void deleteByReviewId(Long reviewId);
}
