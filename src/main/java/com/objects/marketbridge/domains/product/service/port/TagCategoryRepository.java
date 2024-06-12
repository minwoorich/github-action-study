package com.objects.marketbridge.domains.product.service.port;

import com.objects.marketbridge.domains.product.domain.TagCategory;

public interface TagCategoryRepository {
    TagCategory findByName(String name);
    void save(TagCategory tagCategory);
}
