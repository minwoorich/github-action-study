package com.objects.marketbridge.domains.product.infra.productgroup;

import com.objects.marketbridge.domains.product.domain.ProductGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductGroupJpaRepository extends JpaRepository<ProductGroup, Long> {

}
