package com.objects.marketbridge.domains.review.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewProductDto {

    private String name;
    private List<String> options = new ArrayList<>();

    @Builder
    public ReviewProductDto(String name, List<String> options) {
        this.name = name;
        this.options = options;
    }
}
