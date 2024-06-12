package com.objects.marketbridge.domains.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewIdDto {

    private Long reviewId;

    @Builder
    public ReviewIdDto(Long reviewId) {
        this.reviewId = reviewId;
    }
}
