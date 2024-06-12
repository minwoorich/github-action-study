package com.objects.marketbridge.domains.category.infra;

import com.objects.marketbridge.domains.category.domain.Category;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    Optional<Category> findById(Long categoryId);

    @Query(value = "SELECT c FROM Category c WHERE c.parentId=:parentId ")
    Optional<List<Category>> findAllByParentId(@Param("parentId") Long parentId);

}