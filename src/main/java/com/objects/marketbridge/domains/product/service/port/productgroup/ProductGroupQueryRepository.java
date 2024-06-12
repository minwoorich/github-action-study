package com.objects.marketbridge.domains.product.service.port.productgroup;

import com.objects.marketbridge.domains.product.domain.ProductGroup;

import java.util.List;

public interface ProductGroupQueryRepository {

    ProductGroup findById(Long id);

    List<ProductGroup> findAllById(List<Long> ids);

    List<ProductGroup> findAll();
}
