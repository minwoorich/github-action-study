package com.objects.marketbridge.domains.category.service.port;

import com.objects.marketbridge.domains.category.domain.Category;

import java.util.List;

public interface CategoryRepository {

    Category findById(Long id);

    void save(Category category);

    void saveAll(List<Category> categories);

    Category findByName(String name);

    List<Category> findAllByParentId(Long parentId);
}
