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
public class SurveyContent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_content_id")
    private Long id;

    @Column(name = "survey_category_id")
    private Long surveyCategoryId;

    private String content;

    @Builder
    private SurveyContent(Long surveyCategoryId, String content) {
        this.surveyCategoryId = surveyCategoryId;
        this.content = content;
    }
}
