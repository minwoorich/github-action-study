package com.objects.marketbridge.domains.product.service.port;

import com.objects.marketbridge.domains.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository {
    Product findById(Long id);

    Product findByProductNo(String productNo);

    List<Product> findAllById(List<Long> ids);

    List<Product> findByName(String name);

    void deleteAllInBatch();
    Product save(Product product);

    Product saveAndFlush(Product product);

    List<Product> findAll();

    void saveAll(List<Product> products);

    void delete(Product product);

    Page<Product> findAllByCategoryId(Pageable pageable,Long categoryId);
}
