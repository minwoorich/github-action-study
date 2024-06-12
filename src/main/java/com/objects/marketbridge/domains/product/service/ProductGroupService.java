package com.objects.marketbridge.domains.product.service;

import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.domain.ProductToProductGroupService;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import com.objects.marketbridge.domains.product.service.port.productgroup.ProductGroupCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductGroupService {

    private final ProductToProductGroupService productToProductGroupService;
    private final ProductRepository productRepository;
    private final ProductGroupCommandRepository productGroupCommandRepository;

    @Transactional
    public void productToProductGroup() {
        List<Product> products = productRepository.findAll();
        productGroupCommandRepository.saveAll(productToProductGroupService.convert(products));
    }
}
