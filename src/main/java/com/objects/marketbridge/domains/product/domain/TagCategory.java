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
public class TagCategory  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_category_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "tagCategory")
    private List<Tag> tags = new ArrayList<>();

    @Builder
    private TagCategory(String name) {
        this.name = name;
    }

    public void addTags(Tag tag){
        tags.add(tag);
    }

}
