package com.objects.marketbridge.domains.review.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImageDto {

    private Long seqNo;
    private String imgUrl;
    private String description;

    @Builder
    public ReviewImageDto(Long seqNo, String imgUrl, String description) {
        this.seqNo = seqNo;
        this.imgUrl = imgUrl;
        this.description = description;
    }
}
