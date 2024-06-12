package com.objects.marketbridge.domains.review.domain;

import com.objects.marketbridge.domains.member.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_category_id")
    private Long id;

    private Long productId;

    private String name;

    @Builder
    private SurveyCategory(Long productId, String name) {
        this.productId = productId;
        this.name = name;
    }
}
