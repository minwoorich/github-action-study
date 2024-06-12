package com.objects.marketbridge.domains.product.domain;

import com.objects.marketbridge.domains.category.domain.Category;
import com.objects.marketbridge.domains.member.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_group_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Boolean isOwn; // 로켓 true , 오픈 마켓 false

    private String name;

    private Boolean isSubs;

    private Long discountRate;

    @Builder
    private ProductGroup(Category category, Boolean isOwn, String name, Boolean isSubs, Long discountRate) {
        this.category = category;
        this.isOwn = isOwn;
        this.name = name;
        this.isSubs = isSubs;
        this.discountRate = discountRate;
    }

    public static ProductGroup create(Category category, Boolean isOwn, String name, Boolean isSubs, Long discountRate) {
        return ProductGroup.builder()
                .category(category)
                .isOwn(isOwn)
                .name(name)
                .isSubs(isSubs)
                .discountRate(discountRate)
                .build();
    }
}
