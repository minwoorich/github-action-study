package com.objects.marketbridge.domains.product.service.port;

import com.objects.marketbridge.domains.product.domain.Tag;

public interface TagRepository {
    Tag findByName(String name);

    void save(Tag tag);

    Tag findById(Long id);
}
