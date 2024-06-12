package com.objects.marketbridge.domains.product.service.port;

import com.objects.marketbridge.domains.product.domain.ProdOption;

import java.util.List;

public interface ProdOptionRepository {

    void save(ProdOption prodOption);

    void saveAll(List<ProdOption> prodOptions);

    void deleteAllInBatch();
}
