package com.objects.marketbridge.domains.review.dto;

import com.objects.marketbridge.domains.review.domain.ReviewImage;
import com.objects.marketbridge.domains.review.domain.ReviewSurvey;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class GetReviewDto {

    private String productThumbnailUrl;
    private String memberName;
    private Integer rating; //별점, 1~5
//    private String SellerName;
    private String productName;
    private String summary;
    private List<ReviewImageDto> reviewImageDtos = new ArrayList<>();
    private String content;
    private List<GetReviewSurveyDto> getReviewSurveyDtos = new ArrayList<>();

    @Builder
    public GetReviewDto(String productThumbnailUrl, String memberName, Integer rating, String productName, String summary,
                        List<ReviewImageDto> reviewImageDtos, String content,
                        List<GetReviewSurveyDto> getReviewSurveyDtos) {
        this.productThumbnailUrl = productThumbnailUrl;
        this.memberName = memberName;
        this.rating = rating;
        this.productName = productName;
        this.summary = summary;
        this.reviewImageDtos = reviewImageDtos;
        this.content = content;
        this.getReviewSurveyDtos = getReviewSurveyDtos;
    }
}
