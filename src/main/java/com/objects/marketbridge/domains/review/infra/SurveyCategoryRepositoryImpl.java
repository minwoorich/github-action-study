package com.objects.marketbridge.domains.review.infra;

import com.objects.marketbridge.domains.review.domain.SurveyCategory;
import com.objects.marketbridge.domains.review.service.port.SurveyCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SurveyCategoryRepositoryImpl implements SurveyCategoryRepository {

    private final SurveyCategoryJpaRepository surveyCategoryJpaRepository;

    @Override
    public List<SurveyCategory> findAllByProductId(Long productId) {
        return surveyCategoryJpaRepository.findAllByProductId(productId);
    }

    @Override
    public SurveyCategory findByName(String name) {
        return surveyCategoryJpaRepository.findByName(name);
    }
}
