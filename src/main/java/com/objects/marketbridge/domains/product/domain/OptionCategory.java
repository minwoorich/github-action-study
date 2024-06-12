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
public class OptionCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_category_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "optionCategory")
    private List<Option> options = new ArrayList<>();

    @Builder
    private OptionCategory(String name) {
        this.name = name;
    }

    public void addOptions(Option option){
        options.add(option);
    }
}