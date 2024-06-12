package com.objects.marketbridge.domains.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCountDto {

    private Long count;

    @Builder
    public ReviewCountDto(Long count) {
        this.count = count;
    }
}
