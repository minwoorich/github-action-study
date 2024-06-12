package com.objects.marketbridge.domains.payment.service.dto;

import com.objects.marketbridge.domains.product.domain.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductInfoDto {

    private Boolean isOwn;
    private String name;
    private Long price;
    private Boolean isSubs;
    private String thumbImgUrl;
    private Long discountRate;
    private String sellerName;
    private String deliveredDate;

    @Builder
    private ProductInfoDto(Boolean isOwn, String name, Long price, Boolean isSubs, String thumbImgUrl, Long discountRate, String sellerName, String deliveredDate) {
        this.isOwn = isOwn;
        this.name = name;
        this.price = price;
        this.isSubs = isSubs;
        this.thumbImgUrl = thumbImgUrl;
        this.discountRate = discountRate;
        this.sellerName = sellerName;
        this.deliveredDate = deliveredDate;
    }

    // TODO : sellerName 과 deliveredDate 채워넣어야함
    public static ProductInfoDto of(Product product) {
        return ProductInfoDto.builder()
                .isOwn(product.getIsOwn())
                .name(product.getName())
                .price(product.getPrice())
                .isSubs(product.getIsSubs())
                .thumbImgUrl(product.getThumbImg())
                .discountRate(product.getDiscountRate())
                .build();
    }

    public static ProductInfoDto create(Boolean isOwn, String name, Long price, Boolean isSubs, String thumbImgUrl, Long discountRate, String sellerName, String deliveredDate) {
        return ProductInfoDto.builder()
                .isOwn(isOwn)
                .name(name)
                .price(price)
                .isSubs(isSubs)
                .thumbImgUrl(thumbImgUrl)
                .discountRate(discountRate)
                .sellerName(sellerName)
                .deliveredDate(deliveredDate)
                .build();

    }
}
