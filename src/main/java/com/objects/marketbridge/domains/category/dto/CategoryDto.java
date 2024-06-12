package com.objects.marketbridge.domains.category.dto;

import com.objects.marketbridge.common.utils.NestedConvertHelper;
import com.objects.marketbridge.domains.category.domain.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class CategoryDto {

    private Long categoryId;
    private Long parentId;
    private Long level;
    private String name;
    private List<CategoryDto> childCategories;

    @Builder
    public CategoryDto(Long categoryId, Long parentId, Long level, String name) {
        this.categoryId = categoryId;
        this.parentId = parentId;
        this.level = level;
        this.name = name;
        this.childCategories = new ArrayList<>(); // 빈 배열로 초기화
    }

    public static CategoryDto of(Category category){
        return CategoryDto.builder()
                .categoryId(category.getId())
                .parentId(category.getParentId())
                .level(category.getLevel())
                .name(category.getName())
                .build();
    }

    public static List<CategoryDto> toDtoList(List<Category> categories){

        NestedConvertHelper helper = NestedConvertHelper.newInstance(
                categories,
                c -> CategoryDto.of(c),
                c -> c.getParent(),
                c -> c.getId(),
                d -> d.getChildCategories()
        );
        return helper.convert();
    }

    public void addChildCategories(CategoryDto categoryDto){
        childCategories.add(categoryDto);
    }

    public void addChildCategories(List<CategoryDto> categoryDto){
        childCategories.addAll(categoryDto);
    }
}