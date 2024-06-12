package com.objects.marketbridge.domains.product.infra;

import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductRepositoryImplTest {

    @Autowired
    ProductRepository productRepository;

    @AfterEach
    void tearDown(){
        productRepository.deleteAllInBatch();
    }

    @DisplayName("상품명으로 물건찾기")
    @Test
    void findByName(){
        //given
        String productName = "가방";
        productRepository.save(Product.builder().name(productName).build());

        //when
        List<Product> products = productRepository.findByName(productName);

        //then
        assertThat(products).isInstanceOf(List.class);
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo(productName);

    }


}