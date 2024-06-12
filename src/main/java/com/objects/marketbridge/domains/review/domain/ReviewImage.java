package com.objects.marketbridge.domains.review.domain;

import com.objects.marketbridge.domains.image.domain.Image;
import com.objects.marketbridge.domains.member.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    private Long seqNo;

    private String description;

    @Builder
    public ReviewImage(Long id, Review review, Image image, Long seqNo, String description) {
        this.id = id;
        this.review = review;
        this.image = image;
        this.seqNo = seqNo;
        this.description = description;
    }

    public void connectImage(Image image) {
        this.image=image;
    }
}
