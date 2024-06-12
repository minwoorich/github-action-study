package com.objects.marketbridge.domains.product.infra.option;

import com.objects.marketbridge.domains.product.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OptionJpaRepository extends JpaRepository<Option, Long> {

    Optional<Option> findByName(String name);

    Optional<Option> findByNameAndOptionCategoryId(String name, Long optionCategoyId);
}
