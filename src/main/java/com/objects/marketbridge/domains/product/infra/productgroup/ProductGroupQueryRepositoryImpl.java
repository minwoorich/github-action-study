package com.objects.marketbridge.domains.product.infra.productgroup;

import com.objects.marketbridge.domains.product.domain.ProductGroup;
import com.objects.marketbridge.domains.product.service.port.productgroup.ProductGroupQueryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Slf4j
@RequiredArgsConstructor
public class ProductGroupQueryRepositoryImpl implements ProductGroupQueryRepository {

    private final ProductGroupJpaRepository productGroupJpaRepository;

    @Override
    public ProductGroup findById(Long id) {
        return productGroupJpaRepository.findById(id).orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException("엔티티가 존재하지 않습니다. [입력 id = "+id+"]")));
    }

    @Override
    public List<ProductGroup> findAllById(List<Long> ids) {
        return productGroupJpaRepository.findAllById(ids);
    }

    @Override
    public List<ProductGroup> findAll() {
        return productGroupJpaRepository.findAll();
    }
}
