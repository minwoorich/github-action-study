package com.objects.marketbridge.domains.product.dto;

import com.objects.marketbridge.domains.product.domain.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductSimpleDto {
    private Long productId;
    private String prodNo;
    private String thumUrl;
    private String name;
    private Long discountRate;
    private Boolean isOwn;
    private Long price;
    private Long stock;

    @Builder
    private ProductSimpleDto(Long productId,String prodNo, String thumUrl, String name, Long discountRate, Boolean isOwn, Long price, Long stock) {
        this.productId = productId;
        this.prodNo = prodNo;
        this.thumUrl = thumUrl;
        this.name = name;
        this.discountRate = discountRate;
        this.isOwn = isOwn;
        this.price = price;
        this.stock = stock;
    }

    public static ProductSimpleDto of(Product product){
        return ProductSimpleDto.builder()
                .productId(product.getId())
                .prodNo(product.getProductNo())
                .thumUrl(product.getThumbImg())
                .name(product.getName())
                .discountRate(product.getDiscountRate())
                .isOwn(product.getIsOwn())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

}
