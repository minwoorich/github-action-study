package com.objects.marketbridge.domains.review.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateReviewDto {

    private Long reviewId;
    private Integer rating; //별점, 1~5
    private List<UpdateReviewSurveyDto> updateReviewSurveys = new ArrayList<>();
    private String content;
    private List<ReviewImageDto> reviewImages = new ArrayList<>();
    private String summary;

    @Builder
    public UpdateReviewDto(Long reviewId, Integer rating, List<UpdateReviewSurveyDto> updateReviewSurveys, String content, List<ReviewImageDto> reviewImages, String summary) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.updateReviewSurveys = updateReviewSurveys;
        this.content = content;
        this.reviewImages = reviewImages;
        this.summary = summary;
    }
}
