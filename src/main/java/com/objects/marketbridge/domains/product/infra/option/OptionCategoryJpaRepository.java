package com.objects.marketbridge.domains.product.infra.option;

import com.objects.marketbridge.domains.product.domain.OptionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OptionCategoryJpaRepository extends JpaRepository<OptionCategory,Long> {

    Optional<OptionCategory> findByName(String name);
}
