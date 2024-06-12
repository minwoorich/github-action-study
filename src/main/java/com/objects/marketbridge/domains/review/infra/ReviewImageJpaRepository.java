package com.objects.marketbridge.domains.review.infra;

import com.objects.marketbridge.domains.review.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageJpaRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findAllByReviewId(Long reviewId);

//    @Query("DELETE FROM ReviewImage ri WHERE ri IN :reviewImages")
//    void deleteAllByIdInBatch(List<Long> ids);
}
