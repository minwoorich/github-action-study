package com.objects.marketbridge.domains.product.infra.tag;

import com.objects.marketbridge.domains.product.domain.ProdTag;
import com.objects.marketbridge.domains.product.service.port.ProdTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProdTagRepositoryImpl implements ProdTagRepository {
    private final ProdTagJpaRepository prodTagJpaRepository;

    @Override
    public void save(ProdTag prodTag) {
        prodTagJpaRepository.save(prodTag);
    }

    @Override
    public void saveAll(List<ProdTag> prodTags) {
        prodTagJpaRepository.saveAll(prodTags);
    }
}
