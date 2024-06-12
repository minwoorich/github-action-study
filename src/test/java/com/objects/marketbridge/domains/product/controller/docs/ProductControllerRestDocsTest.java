package com.objects.marketbridge.domains.product.controller.docs;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.security.config.SpringSecurityTestConfig;
import com.objects.marketbridge.domains.category.domain.Category;
import com.objects.marketbridge.domains.image.domain.Image;
import com.objects.marketbridge.domains.product.controller.ProductController;
import com.objects.marketbridge.domains.product.controller.request.CreateProductRequestDto;
import com.objects.marketbridge.domains.product.domain.*;
import com.objects.marketbridge.domains.product.dto.ProductDetailDto;
import com.objects.marketbridge.domains.product.dto.ProductSimpleDto;
import com.objects.marketbridge.domains.product.mock.*;
import com.objects.marketbridge.domains.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ProductController.class)
@ExtendWith(RestDocumentationExtension.class)
@ContextConfiguration(classes = {SpringSecurityTestConfig.class})
class ProductControllerRestDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    FakeProductRepository productRepository = new FakeProductRepository();
    FakeCategoryRepository categoryRepository = new FakeCategoryRepository();
    FakeProdOptionRepository prodOptionRepository = new FakeProdOptionRepository();
    FakeProductImageRepository productImageRepository = new FakeProductImageRepository();
    FakeProdTagRepository prodTagRepository = new FakeProdTagRepository();



    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider provider){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(provider))
                .build();
    }

    @Test
    @DisplayName("상품 상세 정보를 조회한다.")
    void getProductDetailByProductIdOk() throws Exception {
        //given
        categoryRepository.save(Category.builder()
                .parentId(1L)
                .name("TEST CATE1")
                .level(1L)
                .build());

        categoryRepository.save(Category.builder()
                .parentId(1L)
                .name("TEST CATE2")
                .level(2L)
                .build());


        Product product1 = Product.builder()
                .productNo("12343213 - 3423333")
                .isOwn(false)
                .price(10000L)
                .name("TEST PRODUCT1")
                .isSubs(true)
                .stock(1000L)
                .thumbImg("MAIN_IMG1.com")
                .discountRate(12L)
                .build();

        product1.setCategory(categoryRepository.findById(2L));
        Product productEntity1 = productRepository.save(product1);
        Product product2 = Product.builder()
                .productNo("12343213 - 4343434")
                .isOwn(false)
                .price(10000L)
                .name("TEST PRODUCT2222")
                .isSubs(true)
                .stock(1000L)
                .thumbImg("MAIN_IMG1.com")
                .discountRate(12L)
                .build();
        product2.setCategory(categoryRepository.findById(2L));
        Product productEntity2 = productRepository.save(product2);

        // 옵션정보 넣기
        addOptionInfo(productEntity1,productEntity2);
        // 이미지 정보 넣기
        addImageInfo(productEntity1,productEntity2);
        // 태그정보 넣기
        addTagInfo(productEntity1,productEntity2);

        ProductDetailDto productDetailDto = ProductDetailDto.builder()
                .categoryInfo("TEST CATE1>TEST CATE2")
                .productId(1L)
                .isOwn(false)
                .price(10000L)
                .name("TEST PRODUCT1")
                .isSubs(true)
                .thumbUrl("MAIN_IMG1.com")
                .discountRate(12L)
                .build();
        Long productId = productEntity1.getId();
        productDetailDto.addAllOptionInfo(prodOptionRepository.findAllByProductId(productId));
        productDetailDto.addAllImageDto(productImageRepository.findAllByProductIdWithImage(productId));
        productDetailDto.addAllTagInfo(prodTagRepository.findAllByProductId(productId));
        List<ProductDetailDto.ProductOptionDto> optionProductInfo = productDetailDto.addAllProductOptionDto(
                productRepository.findAllByProductNoLikeAndProductId(product1.getProductNo().split(" - ")[0],productId));

        optionProductInfo.forEach(item -> {
            item.addAllOptionInfo(prodOptionRepository.findAllByProductId(item.getProductId()));
        });

        given(productService.getProductDetail(anyLong()))
                .willReturn(productDetailDto);

        //when // then
        mockMvc.perform(get("/product/{id}",productId)
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("product-get-detailinfo",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .pathParameters(
                                                parameterWithName("id").description("상품 아이디")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                        .description("응답 데이터"),
                                                fieldWithPath("data.productId").type(JsonFieldType.NUMBER)
                                                        .description("상품 아이디 값"),
                                                fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                                                        .description("상품 가격"),
                                                fieldWithPath("data.discountRate").type(JsonFieldType.NUMBER)
                                                        .description("상품 할인율"),
                                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                                        .description("상품명"),
                                                fieldWithPath("data.thumbUrl").type(JsonFieldType.STRING)
                                                        .description("상품 메인이미지"),
                                                fieldWithPath("data.isOwn").type(JsonFieldType.BOOLEAN)
                                                        .description("마켓브릿지 상품 여부"),
                                                fieldWithPath("data.isSubs").type(JsonFieldType.BOOLEAN)
                                                        .description("구독 가능한 상품 여부"),
                                                fieldWithPath("data.categoryInfo").type(JsonFieldType.STRING)
                                                        .description("카테고리 정보"),
                                                fieldWithPath("data.tagInfos[]").type(JsonFieldType.ARRAY)
                                                        .description("상품 태그 정보"),
                                                fieldWithPath("data.tagInfos[].tagKey").type(JsonFieldType.STRING)
                                                        .description("상품 태그 카테고리 이름"),
                                                fieldWithPath("data.tagInfos[].tagValue").type(JsonFieldType.STRING)
                                                        .description("상품 태그 값"),
                                                fieldWithPath("data.optionInfos[]").type(JsonFieldType.ARRAY).optional()
                                                        .description("상품 옵션 정보"),
                                                fieldWithPath("data.optionInfos[].optionCategory").type(JsonFieldType.STRING).optional()
                                                        .description("상품 옵션 카테고리 이름"),
                                                fieldWithPath("data.optionInfos[].name").type(JsonFieldType.STRING).optional()
                                                        .description("상품 옵션 값"),
                                                fieldWithPath("data.imageInfo[]").type(JsonFieldType.ARRAY)
                                                        .description("상품 이미지 정보"),
                                                fieldWithPath("data.imageInfo[].imgUrl").type(JsonFieldType.STRING)
                                                        .description("이미지 url 정보"),
                                                fieldWithPath("data.imageInfo[].type").type(JsonFieldType.STRING)
                                                        .description("이미지 타입 정보 예) PRODUCT -> 상품이미지, DETAIL-> 상품 설명 이미지"),
                                                fieldWithPath("data.imageInfo[].seqNo").type(JsonFieldType.NUMBER)
                                                        .description("이미지 정렬 순서, PRODUCT의 경우 메인 url 이미지가 맨처음에 와야한다."),
                                                fieldWithPath("data.optionProducts[]").type(JsonFieldType.ARRAY).optional()
                                                        .description("같은 상품의 다른 옵션의 상품 정보"),
                                                fieldWithPath("data.optionProducts[].productId").type(JsonFieldType.NUMBER).optional()
                                                        .description("상품 아이디"),
                                                fieldWithPath("data.optionProducts[].prodNo").type(JsonFieldType.STRING).optional()
                                                        .description("상품 번호"),
                                                fieldWithPath("data.optionProducts[].thumbUrl").type(JsonFieldType.STRING).optional()
                                                        .description("상품 썸네일 이미지"),
                                                fieldWithPath("data.optionProducts[].name").type(JsonFieldType.STRING).optional()
                                                        .description("상품명"),
                                                fieldWithPath("data.optionProducts[].discountRate").type(JsonFieldType.NUMBER).optional()
                                                        .description("상품 할인율"),
                                                fieldWithPath("data.optionProducts[].isOwn").type(JsonFieldType.BOOLEAN).optional()
                                                        .description("마켓브릿지 상품 여부"),
                                                fieldWithPath("data.optionProducts[].price").type(JsonFieldType.NUMBER).optional()
                                                        .description("상품 가격"),
                                                fieldWithPath("data.optionProducts[].stock").type(JsonFieldType.NUMBER).optional()
                                                        .description("상품 재고"),
                                                fieldWithPath("data.optionProducts[].optionInfos[]").type(JsonFieldType.ARRAY).optional()
                                                        .description("상품 옵션 정보"),
                                                fieldWithPath("data.optionProducts[].optionInfos[].optionCategory").type(JsonFieldType.STRING).optional()
                                                        .description("상품 옵션 카테고리 이름"),
                                                fieldWithPath("data.optionProducts[].optionInfos[].name").type(JsonFieldType.STRING).optional()
                                                        .description("상품 옵션 값")
                                        )
                                        .responseSchema(Schema.schema("GetProductDetailByProductIdRes"))
                                        .build()
                        )
                ));
    }

    void addTagInfo(Product product1,Product product2){
        // 태그 카테고리
        TagCategory releaseY = TagCategory.builder()
                .name("출시 연도")
                .build();
        TagCategory releaseS = TagCategory.builder()
                .name("출시 계절")
                .build();
        // 태그
        Tag tagY1 = Tag.builder()
                .tagCategory(releaseY)
                .name("2022년")
                .build();
        Tag tagY2 = Tag.builder()
                .tagCategory(releaseY)
                .name("2023년")
                .build();
        Tag tagS1 = Tag.builder()
                .tagCategory(releaseS)
                .name("가을")
                .build();
        Tag tagS2 = Tag.builder()
                .tagCategory(releaseS)
                .name("봄")
                .build();
        // 상품에 태그 설정
        ProdTag prodTag1 = ProdTag.builder()
                .tag(tagS1)
                .product(product1)
                .build();
        ProdTag prodTag2 = ProdTag.builder()
                .tag(tagY1)
                .product(product1)
                .build();
        ProdTag prodTag3 = ProdTag.builder()
                .tag(tagS2)
                .product(product2)
                .build();
        ProdTag prodTag4 = ProdTag.builder()
                .tag(tagY2)
                .product(product2)
                .build();

        prodTagRepository.saveAll(List.of(prodTag1,prodTag2,prodTag3,prodTag4));
    }

    void addImageInfo(Product product1, Product product2){

        // Image 생성
        Image image = Image.builder()
                .url("testtesttest.com")
                .build();

        ProductImage productImage1 = ProductImage.builder()
                .image(image)
                .product(product1)
                .imgType("PRODUCT")
                .seqNo(1L)
                .build();

        ProductImage productImage2 = ProductImage.builder()
                .image(image)
                .product(product2)
                .imgType("PRODUCT")
                .seqNo(1L)
                .build();

        ProductImage detailImage1 = ProductImage.builder()
                .image(image)
                .product(product1)
                .imgType("DETAIL")
                .seqNo(1L)
                .build();

        ProductImage detailImage2 = ProductImage.builder()
                .image(image)
                .product(product2)
                .imgType("DETAIL")
                .seqNo(1L)
                .build();
        productImageRepository.saveAll(List.of(productImage1,productImage2,detailImage1,detailImage2));
    }

    void addOptionInfo(Product product1,Product product2){
        // 옵션 카테고리
        OptionCategory color = OptionCategory.builder()
                .name("색상")
                .build();
        OptionCategory size = OptionCategory.builder()
                .name("사이즈")
                .build();
        // 옵션
        Option optionS1 = Option.builder()
                .name("M")
                .build();
        optionS1.setOptionCategory(size);
        Option optionC1 = Option.builder()
                .name("네이비")
                .build();
        optionC1.setOptionCategory(color);
        Option optionC2 = Option.builder()
                .name("블랙")
                .build();
        optionC2.setOptionCategory(color);
        Option optionS2 = Option.builder()
                .name("L")
                .build();
        optionS2.setOptionCategory(size);
        // 상품에 옵션 설정
        ProdOption prodOption = ProdOption.builder()
                .option(optionC1)
                .product(product1)
                .build();
        ProdOption prodOption2 = ProdOption.builder()
                .option(optionS1)
                .product(product1)
                .build();
        ProdOption prodOption3 = ProdOption.builder()
                .option(optionC2)
                .product(product2)
                .build();
        ProdOption prodOption4 = ProdOption.builder()
                .option(optionS2)
                .product(product2)
                .build();
        prodOptionRepository.saveAll(List.of(prodOption,prodOption2,prodOption3,prodOption4));
    }

    @Test
    @DisplayName("카테고리별 상품을 조회한다.")
    void getProductByCategoryOk() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0,5);

        categoryRepository.save(Category.builder()
                .parentId(2L)
                .name("TEST CATE")
                .level(1L)
                .build());

        Product product1 = Product.builder()
                .id(1L)
                .productNo("12343213-3423333")
                .isOwn(false)
                .price(10000L)
                .name("TEST PRODUCT1")
                .isSubs(true)
                .stock(1000L)
                .thumbImg("MAIN_IMG1.com")
                .discountRate(12L)
                .build();
        product1.setCategory(categoryRepository.findById(1L));
        productRepository.save(product1);

        Product product2 = Product.builder()
                .id(2L)
                .isOwn(true)
                .name("TEST PRODUCT2")
                .stock(10030L)
                .isSubs(false)
                .price(1001L)
                .thumbImg("MAIN_IMG111.com")
                .discountRate(14L)
                .productNo("12343213-3423434")
                .build();

        product2.setCategory(categoryRepository.findById(1L));
        productRepository.save(product2);

        List<ProductSimpleDto> contents = List.of(ProductSimpleDto.of(product1),ProductSimpleDto.of(product2));

        given(productService.getProductByCategory(any(PageRequest.class),anyString()))
                .willReturn(new PageImpl<>(contents,pageRequest,2));

        //when //then
        mockMvc.perform(get("/product")
                        .param("size","60")
                        .param("page","1")
                        .param("categoryCode","1"
                        )
                        .header("Authorization", "bearer AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-search-by-category",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .queryParameters(
                                        parameterWithName("size").description("한 페이지에 보여질 상품 갯수"),
                                        parameterWithName("page").description("조회 페이지 숫자"),
                                        parameterWithName("categoryCode").description("카테고리 코드 숫자, 해당 카테고리에 속하는 상품 목록 예) 여성패션=9")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                        .description("응답 데이터"),
                                                fieldWithPath("data.content[].productId").type(JsonFieldType.NUMBER)
                                                        .description("상품 아이디"),
                                                fieldWithPath("data.content[].prodNo").type(JsonFieldType.STRING)
                                                        .description("상품 고유 번호"),
                                                fieldWithPath("data.content[].name").type(JsonFieldType.STRING)
                                                        .description("상품 이름"),
                                                fieldWithPath("data.content[].price").type(JsonFieldType.NUMBER)
                                                        .description("상품 가격 (할인 전)"),
                                                fieldWithPath("data.content[].discountRate").type(JsonFieldType.NUMBER)
                                                        .description("상품 할인율"),
                                                fieldWithPath("data.content[].thumUrl").type(JsonFieldType.STRING)
                                                        .description("상품 썸네일 이미지 url"),
                                                fieldWithPath("data.content[].isOwn").type(JsonFieldType.BOOLEAN)
                                                        .description("마켓브릿지 상품인지 입점 판매자 상품인지 판별하는 값"),
                                                fieldWithPath("data.content[].stock").type(JsonFieldType.NUMBER)
                                                        .description("상품 재고"),

                                                fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                                                        .description("페이지 번호"),
                                                fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER)
                                                        .description("페이지당 요소 수"),
                                                fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 비어 있는지 여부"),
                                                fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 되어 있는지 여부"),
                                                fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 되어 있지 않은지 여부"),
                                                fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER)
                                                        .description("페이지 오프셋"),
                                                fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN)
                                                        .description("페이지가 있는지 여부"),
                                                fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
                                                        .description("페이지가 없는지 여부"),
                                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN)
                                                        .description("마지막 페이지 여부"),
                                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                                                        .description("전체 페이지 수"),
                                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER)
                                                        .description("전체 요소 수"),
                                                fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                                        .description("페이지 크기"),
                                                fieldWithPath("data.number").type(JsonFieldType.NUMBER)
                                                        .description("페이지 번호"),
                                                fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 비어 있는지 여부"),
                                                fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 되어 있는지 여부"),
                                                fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 되어 있지 않은지 여부"),
                                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN)
                                                        .description("첫 페이지 여부"),
                                                fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                                                        .description("현재 페이지의 요소 수"),
                                                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN)
                                                        .description("컨텐츠가 비어 있는지 여부")
                                )
                                .responseSchema(Schema.schema("GetProductByCategoryRes"))
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("상품 등록 api 최종 테스트")
    void createNewProduct() throws Exception{
        //given
        List<String> itemImgUrls = new ArrayList<>();
        List<String> detailImgUrls = new ArrayList<>();
        detailImgUrls.addAll(List.of("https://thumbnail7.coupangcdn.com/thumbnails/remote/q89/image/retail/images/2022/10/19/18/6/2e87b707-770c-4e54-9094-bcedfb9d540c.jpg",
                "https://thumbnail6.coupangcdn.com/thumbnails/remote/q89/image/rs_quotation_api/weibrfc9/b793564fe3da48808e4234e7277ff97e.jpg"));
        itemImgUrls.addAll(List.of("https://thumbnail10.coupangcdn.com/thumbnails/remote/492x492ex/image/rs_quotation_api/xael3d49/cd83cb8368d240a0aec9fea44270493f.jpg",
                "https://thumbnail10.coupangcdn.com/thumbnails/remote/492x492ex/image/rs_quotation_api/ribx3yvf/69a7a9f477054210a69a40709d09995f.jpg"));
        Map<String,String> optionInfo =  new HashMap<>();
        Map<String,String> tagInfo = new HashMap<>();
        optionInfo.put("사이즈","M");
        optionInfo.put("색상","네이비");
        tagInfo.put("출시 연도","2022년도");
        tagInfo.put("소재","폴리에스터/나일론");

        CreateProductRequestDto request = CreateProductRequestDto.builder()
                .categoryId(1L)
                .isOwn(false)
                .thumbImg("https://thumbnail9.coupangcdn.com/thumbnails/remote/492x492ex/image/rs_quotation_api/yh93aogx/73e66303ce794816a9659a4684d53c08.jpg")
                .productNo("6854390244 - 16340644607")
                .name("ExampleProduct")
                .discountRate(14L)
                .price(1300L)
                .stock(100L)
                .itemImgUrls(itemImgUrls)
                .detailImgUrls(detailImgUrls)
                .tagInfo(tagInfo)
                .optionInfo(optionInfo)
                .isSubs(false)
                .build();

        given(productService.create(any(CreateProductRequestDto.class))).willReturn(1L);

        //when //then
        mockMvc.perform(post("/product")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "bearer AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .requestFields(
                                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER)
                                                        .description("카테고리 아이디"),
                                                fieldWithPath("isOwn").type(JsonFieldType.BOOLEAN)
                                                        .description("마켓브릿지 상품 여부"),
                                                fieldWithPath("name").type(JsonFieldType.STRING)
                                                        .description("상품명"),
                                                fieldWithPath("price").type(JsonFieldType.NUMBER)
                                                        .description("상품 가격"),
                                                fieldWithPath("isSubs").type(JsonFieldType.BOOLEAN)
                                                        .description("구독 가능 상품 여부"),
                                                fieldWithPath("stock").type(JsonFieldType.NUMBER)
                                                        .description("상품 재고"),
                                                fieldWithPath("thumbImg").type(JsonFieldType.STRING)
                                                        .description("메인 이미지 url"),
                                                fieldWithPath("itemImgUrls").type(JsonFieldType.ARRAY)
                                                        .description("상품 이미지 url리스트"),
                                                fieldWithPath("detailImgUrls").type(JsonFieldType.ARRAY)
                                                        .description("상품 설명 이미지 url리스트"),
                                                fieldWithPath("discountRate").type(JsonFieldType.NUMBER)
                                                        .description("상품 할인율"),
                                                fieldWithPath("productNo").type(JsonFieldType.STRING)
                                                        .description("상품 관리 번호"),
                                                fieldWithPath("optionInfo").type(JsonFieldType.OBJECT)
                                                        .description("상품의 옵션 정보"),
                                                fieldWithPath("optionInfo.색상").type(JsonFieldType.STRING)
                                                        .description("상품의 색상 옵션 정보"),
                                                fieldWithPath("optionInfo.사이즈").type(JsonFieldType.STRING)
                                                        .description("상품의 사이즈 옵션 정보"),
                                                fieldWithPath("tagInfo").type(JsonFieldType.OBJECT)
                                                        .description("상품의 태그 정보"),
                                                fieldWithPath("tagInfo.출시 연도").type(JsonFieldType.STRING)
                                                        .description("상품의 출시 연도 태그 정보"),
                                                fieldWithPath("tagInfo.소재").type(JsonFieldType.STRING)
                                                        .description("상품의 소재 태그 정보")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data").type(JsonFieldType.NUMBER)
                                                        .description("응답 데이터, 등록된 상품 id 값")
                                        )
                                        .requestSchema(Schema.schema("PostProductReq"))
                                        .responseSchema(Schema.schema("PostProductRes"))
                                        .build()
                        )
                        )
                );
    }



}