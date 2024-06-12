package com.objects.marketbridge.domains.product.service.port;

import com.objects.marketbridge.domains.product.dto.ProductImageDto;

import java.util.List;

public interface ProductImageCustomRepository {

    List<ProductImageDto> findAllByProductIdWithImage(Long productId);
}
