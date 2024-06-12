package com.objects.marketbridge.domains.product.infra.option;

import com.objects.marketbridge.domains.product.domain.OptionCategory;
import com.objects.marketbridge.domains.product.service.port.OptionCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class OptionCategoryRepositoryImpl implements OptionCategoryRepository {

    private final OptionCategoryJpaRepository optionCategoryJpaRepository;

    @Override
    public OptionCategory findByName(String name) {
        // 빈 값일 경우 기본값 EMPTY로 반환
        return optionCategoryJpaRepository.findByName(name).orElseGet(() -> OptionCategory.builder().name("EMPTY").build());
    }

    @Override
    public void save(OptionCategory optionCategory) {
        optionCategoryJpaRepository.save(optionCategory);
    }

    @Override
    public OptionCategory findById(Long id) {
        return optionCategoryJpaRepository.findById(id).orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException()));
    }
}
