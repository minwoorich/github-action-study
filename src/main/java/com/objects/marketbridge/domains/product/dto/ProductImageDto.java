package com.objects.marketbridge.domains.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductImageDto {
    private String imgUrl;
    private String type;
    private Long seqNo;

    @Builder
    public ProductImageDto(String imgUrl, String type,Long seqNo) {
        this.imgUrl = imgUrl;
        this.type = type;
        this.seqNo = seqNo;
    }

    public ProductImageDto create(String imgUrl, String type,Long seqNo){
        return ProductImageDto.builder()
                .imgUrl(imgUrl)
                .type(type)
                .seqNo(seqNo)
                .build();
    }
}
