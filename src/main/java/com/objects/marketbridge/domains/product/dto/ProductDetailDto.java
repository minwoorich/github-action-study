package com.objects.marketbridge.domains.product.dto;

import com.objects.marketbridge.domains.product.domain.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class ProductDetailDto {

    // 상품 테이블 정보 가져오기
    private Long productId;
    private Long price;
    private Long discountRate;
    private String name;
    private String thumbUrl;
    private Boolean isOwn;
    private Boolean isSubs;
    // 카테고리 정보 가져오기
    private String categoryInfo;
    // 상품 태그 정보 가져오기
    private List<ProdTagDto> tagInfos;
    // 상품 옵션 정보 가져오기
    private List<OptionDto> optionInfos;
    // 상품 이미지 정보 가져오기
    private List<ProductImageDto> imageInfo;
    // 옵션만 다른 상품 가져오기 (상품 번호 일치)
    private List<ProductOptionDto> optionProducts;
    // 셀러 정보 가져오기 - TODO
    // 찜 리스트 정보 가져오기 - TODO


    @Builder
    private ProductDetailDto(Long productId, Long price, Long discountRate, String name, String thumbUrl, Boolean isOwn, Boolean isSubs, String categoryInfo) {
        this.productId = productId;
        this.price = price;
        this.discountRate = discountRate;
        this.name = name;
        this.thumbUrl = thumbUrl;
        this.isOwn = isOwn;
        this.isSubs = isSubs;
        this.tagInfos = new ArrayList<>();
        this.optionInfos = new ArrayList<>();
        this.imageInfo = new ArrayList<>();
        this.optionProducts = new ArrayList<>();
        this.categoryInfo = categoryInfo;
    }

    public ProductDetailDto create(Long productId, Long price, Long discoutRate, String name, String thumUrl, Boolean isOwn, Boolean isSubs, String categoryInfo) {
        return ProductDetailDto.builder()
                .productId(productId)
                .price(price)
                .discountRate(discoutRate)
                .name(name)
                .thumbUrl(thumUrl)
                .isOwn(isOwn)
                .isSubs(isSubs)
                .categoryInfo(categoryInfo)
                .build();
    }

    public void addAllTagInfo(List<ProdTagDto> prodTagDto){
        this.tagInfos.addAll(prodTagDto);
    }

    public void addAllOptionInfo(List<OptionDto> optionDto){
        this.optionInfos.addAll(optionDto);
    }

    public void addAllImageDto(List<ProductImageDto> productImageDto){
        this.imageInfo.addAll(productImageDto);
    }

    public List<ProductOptionDto> addAllProductOptionDto(List<Product> product){
        List<ProductOptionDto> productOptionDtos = product.stream().map(ProductOptionDto::of).toList();
        this.optionProducts.addAll(productOptionDtos);
        return productOptionDtos;
    }

    @NoArgsConstructor
    @Getter
    public static class ProductOptionDto {
        private Long productId;
        private String prodNo;
        private String thumbUrl;
        private String name;
        private Long discountRate;
        private Boolean isOwn;
        private Long price;
        private Long stock;
        // 상품 옵션 정보 가져오기
        private List<OptionDto> optionInfos;

        @Builder
        private ProductOptionDto(Long productId, String prodNo, String thumbUrl, String name, Long discountRate, Boolean isOwn, Long price, Long stock) {
            this.productId = productId;
            this.prodNo = prodNo;
            this.thumbUrl = thumbUrl;
            this.name = name;
            this.discountRate = discountRate;
            this.isOwn = isOwn;
            this.price = price;
            this.stock = stock;
            this.optionInfos = new ArrayList<>();
        }

        public void addAllOptionInfo(List<OptionDto> optionDto){
            this.optionInfos.addAll(optionDto);
        }

        public static ProductOptionDto of(Product product){
            return ProductOptionDto.builder()
                    .productId(product.getId())
                    .prodNo(product.getProductNo())
                    .thumbUrl(product.getThumbImg())
                    .name(product.getName())
                    .discountRate(product.getDiscountRate())
                    .isOwn(product.getIsOwn())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .build();
        }
    }

}
