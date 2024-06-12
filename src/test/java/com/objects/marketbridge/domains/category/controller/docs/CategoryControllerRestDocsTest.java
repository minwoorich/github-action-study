package com.objects.marketbridge.domains.category.controller.docs;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.security.config.SpringSecurityTestConfig;
import com.objects.marketbridge.domains.category.controller.CategoryController;
import com.objects.marketbridge.domains.category.domain.Category;
import com.objects.marketbridge.domains.category.dto.CategoryDto;
import com.objects.marketbridge.domains.category.service.CategoryService;
import com.objects.marketbridge.domains.product.mock.FakeCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(CategoryController.class)
@ExtendWith(RestDocumentationExtension.class)
@ContextConfiguration(classes = {SpringSecurityTestConfig.class})
public class CategoryControllerRestDocsTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryService categoryService;
    @Autowired
    ObjectMapper objectMapper;

    FakeCategoryRepository categoryRepository = new FakeCategoryRepository();

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider provider){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(provider))
                .build();
    }

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
                .andExpect(status().isOk())
                .andDo(document("category-get-totalinfo",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data[]").type(JsonFieldType.ARRAY)
                                                        .description("응답 데이터"),
                                                fieldWithPath("data[].categoryId").type(JsonFieldType.NUMBER)
                                                        .description("카테고리 아이디"),
                                                fieldWithPath("data[].parentId").type(JsonFieldType.NUMBER)
                                                        .description("부모 카테고리 아이디, 최상위 카테고리는 자신의 아이디값과 같다"),
                                                fieldWithPath("data[].level").type(JsonFieldType.NUMBER)
                                                        .description("카테고리 depth 정보"),
                                                fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                                        .description("카테고리 이름"),
                                                fieldWithPath("data[].childCategories[]").type(JsonFieldType.ARRAY)
                                                        .description("하위 카테고리의 하위 카테고리 리스트 정보"),
                                                fieldWithPath("data[].childCategories[].categoryId").type(JsonFieldType.NUMBER)
                                                        .description("하위 카테고리의 카테고리 아이디"),
                                                fieldWithPath("data[].childCategories[].parentId").type(JsonFieldType.NUMBER)
                                                        .description("부모 카테고리 아이디"),
                                                fieldWithPath("data[].childCategories[].level").type(JsonFieldType.NUMBER)
                                                        .description("하위 카테고리의 depth 정보"),
                                                fieldWithPath("data[].childCategories[].name").type(JsonFieldType.STRING)
                                                        .description("하위 카테고리의 이름"),
                                                fieldWithPath("data[].childCategories[].childCategories[]").type(JsonFieldType.ARRAY)
                                                        .description("3depth 카테고리 리스트 정보"),
                                                fieldWithPath("data[].childCategories[].childCategories[].categoryId").type(JsonFieldType.NUMBER)
                                                        .description("3depth 카테고리 아이디"),
                                                fieldWithPath("data[].childCategories[].childCategories[].parentId").type(JsonFieldType.NUMBER)
                                                        .description("3depth 부모 카테고리 아이디"),
                                                fieldWithPath("data[].childCategories[].childCategories[].level").type(JsonFieldType.NUMBER)
                                                        .description("3depth 카테고리 depth 정보"),
                                                fieldWithPath("data[].childCategories[].childCategories[].name").type(JsonFieldType.STRING)
                                                        .description("3depth 카테고리 이름"),
                                                fieldWithPath("data[].childCategories[].childCategories[].childCategories[]").type(JsonFieldType.ARRAY)
                                                        .description("3depth 카테고리의 하위카테고리 리스트 (빈배열)")
                                        )
                                        .responseSchema(Schema.schema("GetTotalCategoryRes"))
                                        .build()
                        )

                ));
    }
}
