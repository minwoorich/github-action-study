package com.objects.marketbridge.domains.product.domain;

import com.objects.marketbridge.domains.member.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "tag")
    private List<ProdTag> prodTags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_category_id")
    private TagCategory tagCategory;

    @Builder
    private Tag(TagCategory tagCategory,String name) {
        this.tagCategory = tagCategory;
        this.name = name;
    }

    public void addProdTags(ProdTag prodTag){
        prodTags.add(prodTag);
    }
}
