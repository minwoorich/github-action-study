package com.objects.marketbridge.domains.review.domain;

import com.objects.marketbridge.domains.member.domain.BaseEntity;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "review", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<ReviewSurvey> reviewSurveys = new ArrayList<>();

    // 별점
    private Integer rating; //1-5

    private String content;

    private String summary;

    @Builder
    public Review(Long id, Member member, Product product, Integer rating, String content, String summary) {
        this.id = id;
        this.member = member;
        this.product = product;
        this.rating = rating;
        this.content = content;
        this.summary = summary;
    }

    public void update(Integer rating, String content, String summary) {
        this.rating = rating;
        this.content = content;
        this.summary = summary;
    }

    public void addReviewImages(ReviewImage reviewImage) {
        reviewImages.add(reviewImage);
        reviewImage.setReview(this);
    }

    public void addReviewSurveys(ReviewSurvey reviewSurvey) {
        reviewSurveys.add(reviewSurvey);
        reviewSurvey.setReview(this);
    }
}
