package com.objects.marketbridge.domains.product.infra.option;

import com.objects.marketbridge.domains.product.domain.ProdOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdOptionJpaRepository extends JpaRepository<ProdOption, Long> {
}
