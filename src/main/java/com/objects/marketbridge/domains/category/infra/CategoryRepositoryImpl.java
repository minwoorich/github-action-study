package com.objects.marketbridge.domains.category.infra;

import com.objects.marketbridge.domains.category.service.port.CategoryRepository;
import com.objects.marketbridge.domains.category.domain.Category;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Category findById(Long id) {
        return categoryJpaRepository.findById(id)
                .orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException("카테고리가 존재하지 않습니다.")));
    }

    @Override
    public void save(Category category) {
        categoryJpaRepository.save(category);
    }

    @Override
    public void saveAll(List<Category> categories) {
        categoryJpaRepository.saveAll(categories);
    }

    @Override
    public Category findByName(String name) {
        return categoryJpaRepository.findByName(name).orElseGet(() -> Category.builder().name("EMPTY").build());
    }

    @Override
    public List<Category> findAllByParentId(Long parentId) {
        return categoryJpaRepository.findAllByParentId(parentId).orElseGet(ArrayList::new);
    }

}
