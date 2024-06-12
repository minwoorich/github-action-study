package com.objects.marketbridge.domains.product.infra.option;

import com.objects.marketbridge.domains.product.domain.ProdOption;
import com.objects.marketbridge.domains.product.service.port.ProdOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProdOptionRepositoryImpl implements ProdOptionRepository {

    private final ProdOptionJpaRepository prodOptionJpaRepository;

    @Override
    public void save(ProdOption prodOption) {
        prodOptionJpaRepository.save(prodOption);
    }

    @Override
    public void saveAll(List<ProdOption> prodOptions) {
        prodOptionJpaRepository.saveAll(prodOptions);
    }

    @Override
    public void deleteAllInBatch() {
        prodOptionJpaRepository.deleteAllInBatch();
    }
}
