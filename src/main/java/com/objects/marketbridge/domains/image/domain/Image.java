package com.objects.marketbridge.domains.image.domain;

import com.objects.marketbridge.domains.member.domain.BaseEntity;
import com.objects.marketbridge.domains.review.domain.ReviewImage;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String url;

    @OneToOne(mappedBy = "image")
    private ReviewImage reviewImage;

    @Builder
    private Image(String url) {
        this.url = url;
    }

    public void setReviewImage(ReviewImage reviewImage) {
        this.reviewImage = reviewImage;
        reviewImage.connectImage(this);
    }
}
