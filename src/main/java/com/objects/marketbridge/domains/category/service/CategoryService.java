package com.objects.marketbridge.domains.category.service;

import com.objects.marketbridge.domains.category.domain.Category;
import com.objects.marketbridge.domains.category.dto.CategoryDto;
import com.objects.marketbridge.domains.category.service.port.CategoryCustomRepository;
import com.objects.marketbridge.domains.category.service.port.CategoryRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryCustomRepository categoryCustomRepository;

    @Builder
    public CategoryService(CategoryRepository categoryRepository, CategoryCustomRepository categoryCustomRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryCustomRepository = categoryCustomRepository;
    }

    //전체(라지,미디엄,스몰). 라지가 해당 미디엄 전부를, 미디엄이 해당 스몰 전부를 포함하는 형태로 JSON형식.
    public List<CategoryDto> getTotalCategories() {
        List<Category> categoryList = categoryCustomRepository.findAllOrderByParentIdAscNullsFirstCategoryIdAsc();
        return CategoryDto.toDtoList(categoryList);
    }

    // 요청 카테고리의 부모 카테고리 정보 반환
    public String getCategoryInfo(Long categoryId){
        return categoryCustomRepository.findByChildId(categoryId);
    }

    public List<CategoryDto> getLowerCategories(Long categoryId) {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        // 1. 카테고리 아이디 레벨 정보 받아오기
        Category category = categoryRepository.findById(categoryId);
        Long levelInfo = category.getLevel();
        // 2. 1레벨 : 최상위, 2레벨: 2뎁스, 3레벨: 3뎁스
        // 3레벨의 경우 그냥 반환
        if (levelInfo == 3L) {
            CategoryDto categoryDto = CategoryDto.builder()
                    .categoryId(category.getId())
                    .name(category.getName())
                    .level(levelInfo)
                    .parentId(category.getParentId())
                    .build();
            categoryDtoList.add(categoryDto);
        } else {
            // 1레벨의 경우 1레벨을 부모로 갖고있는 2레벨 정보 반환, 1레벨의 경우 2레벨, 3레벨 정보까지 반환
            List<Category> categories = categoryRepository.findAllByParentId(categoryId);
            categoryDtoList = convertToDtoList(categories);
        }

        return categoryDtoList;
    }
    // 하위 카테고리 정보 다 가져오는 메서드
    public CategoryDto getCategoryById(Long categoryId){
        return CategoryDto.of(categoryRepository.findById(categoryId));
    }

    //내부 이용 메서드
    private List<CategoryDto> convertToDtoList(List<Category> categories) {
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : categories) {
            CategoryDto categoryDto = CategoryDto.builder()
                    .categoryId(category.getId())
                    .parentId(category.getParentId())
                    .level(category.getLevel())
                    .name(category.getName())
                    .build();
            // childCategories 필드는 재귀적으로 연관관계 추가
            if (category.getLevel() != 3L) {
                categoryDto.addChildCategories(convertToDtoList(categoryRepository.findAllByParentId(category.getId())));
            }
            categoryDtos.add(categoryDto);
        }
        return categoryDtos;
    }
}