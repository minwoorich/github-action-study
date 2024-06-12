package com.objects.marketbridge.domains.product.service.port.productgroup;

import com.objects.marketbridge.domains.product.domain.ProductGroup;

import java.util.List;

public interface ProductGroupCommandRepository {

    void save(ProductGroup productGroup);

    void saveAll(List<ProductGroup> productGroups);
}
