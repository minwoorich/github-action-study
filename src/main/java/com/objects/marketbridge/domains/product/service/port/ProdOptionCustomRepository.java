package com.objects.marketbridge.domains.product.service.port;


import com.objects.marketbridge.domains.product.dto.OptionDto;

import java.util.List;

public interface ProdOptionCustomRepository {
    List<OptionDto> findAllByProductId(Long productId);
}
