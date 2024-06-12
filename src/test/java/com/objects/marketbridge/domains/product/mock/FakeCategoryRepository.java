package com.objects.marketbridge.domains.product.mock;

import com.objects.marketbridge.domains.category.domain.Category;
import com.objects.marketbridge.domains.category.dto.CategoryDto;
import com.objects.marketbridge.domains.category.service.port.CategoryRepository;
import com.objects.marketbridge.domains.category.domain.Category;
import com.objects.marketbridge.domains.category.service.port.CategoryCustomRepository;
import com.objects.marketbridge.domains.category.service.port.CategoryRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class FakeCategoryRepository implements CategoryRepository, CategoryCustomRepository {

    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    List<Category> data = Collections.synchronizedList(new ArrayList<>());


    @Override
    public Category findById(Long id) {
        return data.stream().filter(item -> item.getId().equals(id)).findAny().orElseThrow();
    }

    @Override
    public void save(Category category) {
        if (category.getId() == null || category.getId() == 0){
            data.add(Category.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .parent(category.getParent())
                    .level(category.getLevel())
                    .name(category.getName())
                    .build());
        } else {
            data.removeIf(item -> Objects.equals(item.getId(), category.getId()));
            data.add(category);
        }
    }

    @Override
    public void saveAll(List<Category> categories) {
        for (Category category: categories) {
            save(category);
        }
    }

    @Override
    public Category findByName(String name) {
        return null;
    }


    @Override
    public List<Category> findAllByParentId(Long parentId) {
        return null;
    }

    @Override
    public List<Category> findAllOrderByParentIdAscNullsFirstCategoryIdAsc() {
        return null;
    }

    @Override
    public String findByChildId(Long categoryId) {
        List<String> result = new ArrayList<>();

        for (Category category:data) {
            if (category.getId().equals(category.getParent().getId()) ||
            category.getId()==categoryId ){
                result.add(category.getName());
            }
        }

        return String.join(">",result);
    }

}
