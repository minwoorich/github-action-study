package com.objects.marketbridge.domains.product.infra.product;

import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

import java.util.List;


@RequiredArgsConstructor
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final EntityManager entityManager;

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productJpaRepository.findById(id).orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException("해당 상품 엔티티가 존재하지 않습니다. 입력 id = "+id)));
    }

    @Override
    public Product findByProductNo(String productNo) {
        return productJpaRepository.findByProductNo(productNo).orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException()));
    }

    @Override
    public List<Product> findAllById(List<Long> ids) {
        return productJpaRepository.findAllById(ids);
    }

    @Override
    public List<Product> findByName(String name) {
        return productJpaRepository.findByName(name);
    }

    @Override
    public void deleteAllInBatch() {
        productJpaRepository.deleteAllInBatch();
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Product saveAndFlush(Product product) {
        return productJpaRepository.saveAndFlush(product);
    }

    @Override
    public void saveAll(List<Product> products) {
        productJpaRepository.saveAll(products);
    }

    @Override
    public void delete(Product product) {
        productJpaRepository.delete(product);
    }

    @Override
    public Page<Product> findAllByCategoryId(Pageable pageable,Long categoryId) {
        return productJpaRepository.findAllByCategoryId(pageable,categoryId);
    }

}
