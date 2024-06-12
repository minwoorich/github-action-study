package com.objects.marketbridge.domains.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OptionDto {

    private String optionCategory;
    private String name;

    @Builder
    private OptionDto(String optionCategory, String name) {
        this.optionCategory = optionCategory;
        this.name = name;
    }

    public OptionDto create(String optionCategory, String name){
        return OptionDto.builder()
                .optionCategory(optionCategory)
                .name(name)
                .build();
    }
}
