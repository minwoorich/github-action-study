package com.objects.marketbridge.domains.review.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetReviewImageDto {

    private Long reviewImageId;
    private Long imageId;
    private Long seqNo;
    private String imgUrl;
    private String description;
}
