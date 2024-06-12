package com.objects.marketbridge.domains.category.controller;

import com.objects.marketbridge.common.security.config.SpringSecurityTestConfig;
import com.objects.marketbridge.domains.category.domain.Category;
import com.objects.marketbridge.domains.category.dto.CategoryDto;
import com.objects.marketbridge.domains.category.service.CategoryService;
import com.objects.marketbridge.domains.product.mock.FakeCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(CategoryController.class)
@ContextConfiguration(classes = {SpringSecurityTestConfig.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryService categoryService;

    FakeCategoryRepository categoryRepository = new FakeCategoryRepository();

    @Test
    @DisplayName("전체 카테고리를 조회한다.")
    void getTotalCategoryInfo() throws Exception {
        //given
        Category category1 = Category.builder()
                .id(1L)
                .parentId(1L)
                .name("TEST CATE1")
                .level(1L)
                .build();
        category1.setParentId(category1);
        categoryRepository.save(category1);
        Category category2 = Category.builder()
                .id(2L)
                .parentId(1L)
                .name("TEST CATE1")
                .level(2L)
                .build();
        category2.setParentId(category1);
        categoryRepository.save(category2);
        Category category3 = Category.builder()
                .id(3L)
                .parentId(2L)
                .name("TEST CATE1")
                .level(3L)
                .build();
        category3.setParentId(category2);
        categoryRepository.save(category3);
        Category category4 = Category.builder()
                .id(4L)
                .parentId(2L)
                .name("TEST CATE1")
                .level(3L)
                .build();
        category4.setParentId(category2);
        categoryRepository.save(category4);



        List<CategoryDto> categoryDto = CategoryDto.toDtoList(List.of(category1,category2,category3,category4));


        given(categoryService.getTotalCategories())
                .willReturn(categoryDto);

        //when //then
        mockMvc.perform(get("/category/total")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}