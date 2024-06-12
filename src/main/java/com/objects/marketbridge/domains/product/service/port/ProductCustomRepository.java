package com.objects.marketbridge.domains.product.service.port;

import com.objects.marketbridge.domains.product.domain.Product;

import java.util.List;

public interface ProductCustomRepository {

    Product findByIdwithCategory(Long id);
    List<Product> findAllByProductNoLikeAndProductId(String productNo, Long productId);
}
