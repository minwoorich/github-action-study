package com.objects.marketbridge.domains.product.service.port;

import com.objects.marketbridge.domains.product.domain.ProdTag;

import java.util.List;

public interface ProdTagRepository {
    void save(ProdTag prodTag);

    void saveAll(List<ProdTag> prodTags);
}
