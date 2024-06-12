package com.objects.marketbridge.domains.product.service.port;

import com.objects.marketbridge.domains.product.domain.OptionCategory;

public interface OptionCategoryRepository {
    OptionCategory findByName(String name);

    void save(OptionCategory optionCategory);

    OptionCategory findById(Long id);
}
