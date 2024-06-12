package com.objects.marketbridge.domains.review.infra;

import com.objects.marketbridge.domains.review.domain.SurveyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyCategoryJpaRepository extends JpaRepository<SurveyCategory, Long> {

    List<SurveyCategory> findAllByProductId(Long productId);

    SurveyCategory findByName(String name);
}
