package com.objects.marketbridge.domains.payment.service.dto;


import com.objects.marketbridge.domains.payment.service.dto.ProductInfoDto;
import com.objects.marketbridge.domains.product.domain.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductInfoDtoTest {

    @DisplayName("Product를 받아서 ProductInfoDto를 만들 수 있어야한다.(sellerName, deliveredDate 제외")
    @Test
    void of() {
        // given
        // TODO : sellerName, deliveredDate 추가해야함
        Product product = Product.builder()
                .isOwn(false)
                .isSubs(false)
                .name("가방")
                .price(4000L)
                .thumbImg("thumbImg")
                .discountRate(3L)
                .stock(100L)
                .productNo("productNo")
                .build();

        // when
        ProductInfoDto productInfo = ProductInfoDto.of(product);

        //then
        Assertions.assertThat(productInfo).extracting(
                "isOwn",
                "isSubs",
                "name",
                "price",
                "thumbImgUrl",
                "discountRate")
                .containsExactlyInAnyOrder(
                product.getIsOwn(),
                product.getIsSubs(),
                product.getName(),
                product.getPrice(),
                product.getThumbImg(),
                product.getDiscountRate()
        );

    }
}