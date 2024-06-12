package com.objects.marketbridge.domains.product.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductToProductGroupService {

    public List<ProductGroup> convert(List<Product> products) {
        return products.stream()
                .collect(Collectors.groupingBy(Product::getName))
                .values().stream()
                .map(this::createProductGroupFromProducts)
                .collect(Collectors.toList());
    }

    private ProductGroup createProductGroupFromProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            log.error("Product list is null or empty, cannot create ProductGroup.");
            throw new IllegalArgumentException("Cannot create ProductGroup from an empty or null product list.");
        }

        Product representative = products.get(0); // Using the first product as representative of the group.

        try {
            return ProductGroup.create(
                    representative.getCategory(),
                    representative.getIsOwn(),
                    representative.getName(),
                    representative.getIsSubs(),
                    representative.getDiscountRate());
        } catch (Exception e) {
            log.error("Error creating ProductGroup: {}", e.getMessage(), e);
            throw e;
        }
    }
}
