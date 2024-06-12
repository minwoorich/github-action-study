package com.objects.marketbridge.domains.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProdTagDto {

    private String tagKey;
    private String tagValue;

    @Builder
    private ProdTagDto(String tagKey, String tagValue) {
        this.tagKey = tagKey;
        this.tagValue = tagValue;
    }

    public ProdTagDto create(String tagKey, String tagValue){
        return ProdTagDto.builder()
                .tagKey(tagKey)
                .tagValue(tagValue)
                .build();
    }
}
