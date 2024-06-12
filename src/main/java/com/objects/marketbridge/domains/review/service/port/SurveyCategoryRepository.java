package com.objects.marketbridge.domains.review.service.port;

import com.objects.marketbridge.domains.review.domain.SurveyCategory;

import java.util.List;

public interface SurveyCategoryRepository {

    List<SurveyCategory> findAllByProductId(Long productId);

    SurveyCategory findByName(String name);
}
