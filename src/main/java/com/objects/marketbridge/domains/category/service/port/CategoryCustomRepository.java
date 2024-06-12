package com.objects.marketbridge.domains.category.service.port;

import com.objects.marketbridge.domains.category.domain.Category;
import com.objects.marketbridge.domains.category.dto.CategoryDto;

import java.util.List;

public interface CategoryCustomRepository {

    List<Category> findAllOrderByParentIdAscNullsFirstCategoryIdAsc();

    String findByChildId(Long categoryId);

}
