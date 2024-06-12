package com.objects.marketbridge.domains.product.infra.tag;

import com.objects.marketbridge.domains.product.domain.TagCategory;
import com.objects.marketbridge.domains.product.service.port.TagCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TagCategoryImpl implements TagCategoryRepository {
    private final TagCategoryJpaRepository tagCategoryJpaRepository;

    @Override
    public TagCategory findByName(String name) {
        return tagCategoryJpaRepository.findByName(name).orElseGet(() -> TagCategory.builder().name("EMPTY").build());
    }

    @Override
    public void save(TagCategory tagCategory) {
        tagCategoryJpaRepository.save(tagCategory);
    }
}
