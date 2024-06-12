package com.objects.marketbridge.domains.product.infra.productgroup;

import com.objects.marketbridge.domains.product.domain.ProductGroup;
import com.objects.marketbridge.domains.product.service.port.productgroup.ProductGroupCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductGroupCommandRepositoryImpl implements ProductGroupCommandRepository {

    private final ProductGroupJpaRepository productGroupJpaRepository;

    @Override
    public void save(ProductGroup productGroup) {
        productGroupJpaRepository.save(productGroup);
    }

    @Override
    public void saveAll(List<ProductGroup> productGroups) {
        productGroupJpaRepository.saveAll(productGroups);
    }
}
