package com.objects.marketbridge.domains.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewableDto {

    private String productThumbnailUrl;
    private String productName;
    private LocalDateTime deliveredDate;

    @Builder
    public ReviewableDto(String productThumbnailUrl, String productName, LocalDateTime deliveredDate) {
        this.productThumbnailUrl = productThumbnailUrl;
        this.productName = productName;
        this.deliveredDate = deliveredDate;
    }
}
