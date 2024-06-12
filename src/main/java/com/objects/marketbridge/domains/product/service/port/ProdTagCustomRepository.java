package com.objects.marketbridge.domains.product.service.port;


import com.objects.marketbridge.domains.product.dto.ProdTagDto;

import java.util.List;

public interface ProdTagCustomRepository {

    List<ProdTagDto> findAllByProductId(Long productId);
}
