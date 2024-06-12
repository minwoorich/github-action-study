package com.objects.marketbridge.domains.product.infra.product;

import com.objects.marketbridge.domains.product.domain.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id);
    Optional<Product> findByProductNo(String productNo);
    List<Product> findByName(String name);

    @Query( value = "WITH RECURSIVE CTE AS ( " +
            "SELECT " +
            "category_id, " +
            "parent_id " +
            "FROM category " +
            "WHERE category_id = :categoryId " +
            "UNION ALL " +
            "SELECT " +
            "c.category_id, " +
            "c.parent_id " +
            "FROM category c " +
            "INNER JOIN CTE b ON c.parent_id = b.category_id " +
            ") " +
            "select * " +
            "from product p " +
            "where p.category_id in (select category_id from CTE)",
            countQuery = "WITH RECURSIVE CTE AS ( " +
                    "SELECT " +
                    "category_id, " +
                    "parent_id " +
                    "FROM category " +
                    "WHERE category_id = :categoryId " +
                    "UNION ALL " +
                    "SELECT " +
                    "c.category_id, " +
                    "c.parent_id " +
                    "FROM category c " +
                    "INNER JOIN CTE b ON c.parent_id = b.category_id " +
                    ") " +
                    "select count(*) " +
                    "from product p " +
                    "where p.category_id in (select category_id from CTE)",
            nativeQuery = true
    )
    Page<Product> findAllByCategoryId(Pageable pageable,@Param("categoryId") Long categoryId);
}
