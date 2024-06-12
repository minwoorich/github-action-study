package com.objects.marketbridge.domains.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewLikeCountDto {
    public Long count;

    @Builder
    public ReviewLikeCountDto(Long count) {
        this.count = count;
    }
}
