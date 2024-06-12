package com.objects.marketbridge.domains.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewLikeDto {

    private Long reviewId;
    private Long memberId;

    @Builder
    public ReviewLikeDto(Long reviewId, Long memberId, Boolean liked) {
        this.reviewId = reviewId;
        this.memberId = memberId;
    }
}