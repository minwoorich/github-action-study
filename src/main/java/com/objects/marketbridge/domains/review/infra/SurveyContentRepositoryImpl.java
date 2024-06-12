package com.objects.marketbridge.domains.review.infra;

import com.objects.marketbridge.domains.review.domain.SurveyContent;
import com.objects.marketbridge.domains.review.service.port.SurveyContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SurveyContentRepositoryImpl implements SurveyContentRepository {

    private final SurveyContentJpaRepository surveyContentJpaRepository;

    @Override
    public List<SurveyContent> findAllBySurveyCategoryId(Long surveyCategoryId) {
        return surveyContentJpaRepository.findAllBySurveyCategoryId(surveyCategoryId);
    }

    @Override
    public Boolean existsBySurveyCategoryId(Long surveyCategoryId) {
        return surveyContentJpaRepository.existsBySurveyCategoryId(surveyCategoryId);
    }

    @Override
    public List<SurveyContent> findAllBySurveyCategoryIdIn(List<Long> surveyCategoryIds) {
        return surveyContentJpaRepository.findAllBySurveyCategoryIdIn(surveyCategoryIds);
    }
}
