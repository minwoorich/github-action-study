package com.objects.marketbridge.domains.member.domain;

import com.objects.marketbridge.domains.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    @DisplayName("주어진 수량만큼 재고가 증가해야 한다.")
    public void increase() {
        // given
        Product product = Product.builder()
                .stock(10L)
                .build();
        Long quantity = 5L;

        // when
        product.increase(quantity);

        // then
        assertThat(product.getStock()).isEqualTo(15L);
    }
}