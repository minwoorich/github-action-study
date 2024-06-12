package com.objects.marketbridge.domains.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewSurveyCategoryContentsDto {

    private String category;
    private List<String> contents;

    @Builder
    public ReviewSurveyCategoryContentsDto(String category, List<String> contents) {
        this.category = category;
        this.contents = contents;
    }
}
