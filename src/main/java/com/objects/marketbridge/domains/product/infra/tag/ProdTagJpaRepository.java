package com.objects.marketbridge.domains.product.infra.tag;

import com.objects.marketbridge.domains.product.domain.ProdTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdTagJpaRepository extends JpaRepository<ProdTag,Long> {
}
