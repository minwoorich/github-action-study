package com.objects.marketbridge.domains.product.infra.tag;

import com.objects.marketbridge.domains.product.domain.TagCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagCategoryJpaRepository extends JpaRepository<TagCategory,Long> {

    Optional<TagCategory> findByName(String name);
}
