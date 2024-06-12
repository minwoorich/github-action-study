package com.objects.marketbridge.domains.review.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetReviewSurveyDto {
    private String surveyCategoryName;
    private String content;

    @Builder
    public GetReviewSurveyDto(String surveyCategoryName, String content) {
        this.surveyCategoryName = surveyCategoryName;
        this.content = content;
    }
}
