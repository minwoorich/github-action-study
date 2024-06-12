package com.objects.marketbridge.domains.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.security.config.SpringSecurityTestConfig;
import com.objects.marketbridge.domains.category.domain.Category;
import com.objects.marketbridge.domains.image.domain.Image;
import com.objects.marketbridge.domains.product.controller.request.CreateProductRequestDto;
import com.objects.marketbridge.domains.product.domain.*;
import com.objects.marketbridge.domains.product.dto.ProductDetailDto;
import com.objects.marketbridge.domains.product.dto.ProductSimpleDto;
import com.objects.marketbridge.domains.product.mock.*;
import com.objects.marketbridge.domains.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = {SpringSecurityTestConfig.class})
class ProductControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private ProductService productService;
    @Autowired private ObjectMapper objectMapper;
    FakeProductRepository productRepository = new FakeProductRepository();
    FakeCategoryRepository categoryRepository = new FakeCategoryRepository();
    FakeProdOptionRepository prodOptionRepository = new FakeProdOptionRepository();
    FakeProductImageRepository productImageRepository = new FakeProductImageRepository();
    FakeProdTagRepository prodTagRepository = new FakeProdTagRepository();

    @Test
    @DisplayName("상품 상세 정보를 조회한다.")
    void getProductDetailByProductId() throws Exception {
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
        productRepository.saveAll(List.of(product1,product2));

        // 옵션정보 넣기
        addOptionInfo(product1,product2);
        // 이미지 정보 넣기
        addImageInfo(product1,product2);
        // 태그정보 넣기
        addTagInfo(product1,product2);

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
        productDetailDto.addAllOptionInfo(prodOptionRepository.findAllByProductId(product1.getId()));
        productDetailDto.addAllImageDto(productImageRepository.findAllByProductIdWithImage(product1.getId()));
        productDetailDto.addAllTagInfo(prodTagRepository.findAllByProductId(product1.getId()));
        List<ProductDetailDto.ProductOptionDto> optionProductInfo = productDetailDto.addAllProductOptionDto(productRepository.findAllByProductNoLikeAndProductId(product1.getProductNo().split(" - ")[0],product1.getId()));

        optionProductInfo.forEach(item -> {
            item.addAllOptionInfo(prodOptionRepository.findAllByProductId(item.getProductId()));
        });

        given(productService.getProductDetail(anyLong()))
                .willReturn(productDetailDto);
        //when //then
        mockMvc.perform(get("/product/{id}",1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
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
    void getProductByCategory() throws Exception {

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
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("신규 상품을 등록한다.")
    @Test
    void createProduct() throws Exception{
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
                .andExpect(status().isOk());
    }
}