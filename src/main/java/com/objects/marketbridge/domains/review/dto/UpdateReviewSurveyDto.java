package com.objects.marketbridge.domains.review.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateReviewSurveyDto {
    private Long reviewSurveyId;
    private String content;

    @Builder
    public UpdateReviewSurveyDto(Long reviewSurveyId, String content) {
        this.reviewSurveyId = reviewSurveyId;
        this.content = content;
    }
}
