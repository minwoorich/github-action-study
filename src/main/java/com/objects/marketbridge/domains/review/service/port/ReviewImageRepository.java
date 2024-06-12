package com.objects.marketbridge.domains.review.service.port;

import com.objects.marketbridge.domains.review.domain.ReviewImage;

import java.util.List;

public interface ReviewImageRepository {

    void save(ReviewImage reviewImage);

    List<ReviewImage> findAllByReviewId(Long reviewId);

    void delete(ReviewImage reviewImage);

    void saveAll(List<ReviewImage> reviewImages);

    void deleteAllByIdInBatch(List<Long> ids);
}
