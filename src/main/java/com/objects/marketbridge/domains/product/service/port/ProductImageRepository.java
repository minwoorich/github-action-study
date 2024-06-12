package com.objects.marketbridge.domains.product.service.port;

import com.objects.marketbridge.domains.product.domain.ProductImage;

import java.util.List;

public interface ProductImageRepository {

    void save(ProductImage productImage);

    List<ProductImage> findAllByProductId(Long productId);

    void delete(ProductImage productImage);

    void saveAll(List<ProductImage> productImages);
}
