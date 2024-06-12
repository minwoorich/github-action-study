package com.objects.marketbridge.domains.product.infra.option;

import com.objects.marketbridge.domains.product.domain.Option;
import com.objects.marketbridge.domains.product.service.port.OptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepository {

    private final OptionJpaRepository optionJpaRepository;

    @Override
    public void save(Option option) {
        optionJpaRepository.save(option);
    }

    @Override
    public Option findById(Long id) {
        return optionJpaRepository.findById(id).orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException()));
    }

    @Override
    public Option findByName(String name) {
        // 없는 경우 기본값 EMPTY로 반환
        return optionJpaRepository.findByName(name).orElseGet(()-> Option.builder().name("EMPTY").build());
    }

    @Override
    public Option findByNameAndOptionCategoryId(String name, Long optionCategoyId) {
        return optionJpaRepository.findByNameAndOptionCategoryId(name,optionCategoyId).orElseGet(()-> Option.builder().name("EMPTY").build());
    }

    @Override
    public void deleteAllInBatch() {
        optionJpaRepository.deleteAllInBatch();
    }

    @Override
    public void saveAll(List<Option> options) {
        optionJpaRepository.saveAll(options);
    }
}
