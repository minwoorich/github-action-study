package com.objects.marketbridge.domains.review.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateReviewSurveyDto {

    private Long surveyCategoryId;
    private String surveyCategoryName;
    private String content;

    @Builder
    public CreateReviewSurveyDto(Long surveyCategoryId, String surveyCategoryName, String content) {
        this.surveyCategoryId = surveyCategoryId;
        this.surveyCategoryName = surveyCategoryName;
        this.content = content;
    }
}
