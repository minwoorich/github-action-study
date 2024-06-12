package com.objects.marketbridge.domains.review.service.port;

import com.objects.marketbridge.domains.review.domain.SurveyContent;

import java.util.List;

public interface SurveyContentRepository {

    List<SurveyContent> findAllBySurveyCategoryId(Long surveyCategoryId);

    Boolean existsBySurveyCategoryId(Long surveyCategoryId);

    List<SurveyContent> findAllBySurveyCategoryIdIn(List<Long> surveyCategoryIds);
}
