package com.objects.marketbridge.domains.review.infra;

import com.objects.marketbridge.domains.review.domain.SurveyContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyContentJpaRepository extends JpaRepository<SurveyContent, Long> {

    List<SurveyContent> findAllBySurveyCategoryId(Long surveyCategoryId);

    Boolean existsBySurveyCategoryId(Long surveyCategoryId);

    List<SurveyContent> findAllBySurveyCategoryIdIn(List<Long> surveyCategoryIds);
}
