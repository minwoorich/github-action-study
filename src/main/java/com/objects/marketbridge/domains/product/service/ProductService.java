package com.objects.marketbridge.domains.product.service;

import com.objects.marketbridge.domains.category.service.CategoryService;
import com.objects.marketbridge.domains.category.service.port.CategoryRepository;
import com.objects.marketbridge.domains.image.domain.Image;
import com.objects.marketbridge.domains.image.infra.ImageRepository;
import com.objects.marketbridge.domains.product.controller.request.CreateProductRequestDto;
import com.objects.marketbridge.domains.product.domain.*;
import com.objects.marketbridge.domains.product.dto.*;
import com.objects.marketbridge.domains.product.service.port.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ProductImageRepository productImageRepository;
    private final OptionRepository optionRepository;
    private final ProdOptionRepository prodOptionRepository;
    private final OptionCategoryRepository optionCategoryRepository;
    private final TagCategoryRepository tagCategoryRepository;
    private final TagRepository tagRepository;
    private final ProdTagRepository prodTagRepository;
    private final CategoryService categoryService;
    private final ProductCustomRepository productCustomRepository;
    private final ProdOptionCustomRepository prodOptionCustomRepository;
    private final ProdTagCustomRepository prodTagCustomRepository;
    private final ProductImageCustomRepository productImageCustomRepository;

    @Builder
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ImageRepository imageRepository, ProductImageRepository productImageRepository, OptionRepository optionRepository, ProdOptionRepository prodOptionRepository, OptionCategoryRepository optionCategoryRepository, TagCategoryRepository tagCategoryRepository, TagRepository tagRepository, ProdTagRepository prodTagRepository, CategoryService categoryService, ProductCustomRepository productCustomRepository, ProdOptionCustomRepository prodOptionCustomRepository, ProdTagCustomRepository prodTagCustomRepository, ProductImageCustomRepository productImageCustomRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.productImageRepository = productImageRepository;
        this.optionRepository = optionRepository;
        this.prodOptionRepository = prodOptionRepository;
        this.optionCategoryRepository = optionCategoryRepository;
        this.tagCategoryRepository = tagCategoryRepository;
        this.tagRepository = tagRepository;
        this.prodTagRepository = prodTagRepository;
        this.categoryService = categoryService;
        this.productCustomRepository = productCustomRepository;
        this.prodOptionCustomRepository = prodOptionCustomRepository;
        this.prodTagCustomRepository = prodTagCustomRepository;
        this.productImageCustomRepository = productImageCustomRepository;
    }

    // 상품 상세 정보 조회
    @Transactional
    public ProductDetailDto getProductDetail(Long id) {
        // 상품 테이블 정보 가져오기
        Product product = productCustomRepository.findByIdwithCategory(id);
        Long productId = product.getId();
        // 카테고리 정보 가져오기
        String category = categoryService.getCategoryInfo(product.getCategory().getId());
        // DTO 생성
        ProductDetailDto productDetailDto
                = ProductDetailDto.builder()
                .productId(product.getId())
                .price(product.getPrice())
                .discountRate(product.getDiscountRate())
                .name(product.getName())
                .thumbUrl(product.getThumbImg())
                .isOwn(product.getIsOwn())
                .isSubs(product.getIsSubs())
                .categoryInfo(category)
                .build();

        // 상품 옵션 정보 가져오기
        List<OptionDto> optionInfo = prodOptionCustomRepository.findAllByProductId(productId);
        productDetailDto.addAllOptionInfo(optionInfo);
        // 상품 태그 정보 가져오기
        List<ProdTagDto> tagInfo = prodTagCustomRepository.findAllByProductId(productId);
        productDetailDto.addAllTagInfo(tagInfo);
        // 상품 이미지 정보 가져오기
        productDetailDto.addAllImageDto(productImageCustomRepository.findAllByProductIdWithImage(productId));
        // 옵션만 다른 상품 가져오기 (상품 번호 일치)
        List<Product> optionProduct = productCustomRepository.findAllByProductNoLikeAndProductId(product.getProductNo().split(" - ")[0],productId);
        List<ProductDetailDto.ProductOptionDto> optionProductInfo = productDetailDto.addAllProductOptionDto(optionProduct);
        // 옵션만 다른 상품에 옵션값 집어넣기
        optionProductInfo.forEach(item -> {
            item.addAllOptionInfo(prodOptionCustomRepository.findAllByProductId(item.getProductId()));
        });

        return productDetailDto;
    }


    // category 기준 상품 조회
    @Transactional
    public Page<ProductSimpleDto> getProductByCategory(Pageable pageable, String categoryId){
        // 주어진 category 정보에 해당되는 상품 조회
        Page<Product> productList = productRepository.findAllByCategoryId(pageable,categoryService.getCategoryById(Long.valueOf(categoryId)).getCategoryId());
        // product -> productSimpleDto
        List<ProductSimpleDto> productSimpleDtos = new ArrayList<>();
        for (Product product: productList) {
            productSimpleDtos.add(ProductSimpleDto.of(product));
        }
        return new PageImpl<>(productSimpleDtos, pageable, productList.getTotalElements());
    }

    // 상품 생성
    @Transactional
    public Long create(CreateProductRequestDto request){
        CreateProductDto createProductDto =CreateProductDto.fromRequest(request);
        //1. 상품 생성
        Product product = productRepository.save(createProduct(createProductDto));

        //2. productImage 저장
        // 2-1. imageurl -> ProductImage 화
        List<ProductImage> productImages = createProductImages(createProductDto.getProductImageList(),product);
        productImageRepository.saveAll(productImages);

        //3. 옵션 추가
        List<ProdOption> prodOptions = createProdOptions(createProductDto.getOptionInfoList(),product);
        prodOptionRepository.saveAll(prodOptions);
        //4. tag 추가
        List<ProdTag> prodTags = createTags(createProductDto.getProdTagList(), product);
        prodTagRepository.saveAll(prodTags);

        //5. product id 반환
        return product.getId();
    }

    public Product createProduct(CreateProductDto createProductDto){
        Boolean isOwn = createProductDto.getIsOwn();
        String name = createProductDto.getName();
        Long price = createProductDto.getPrice();
        Boolean isSubs = createProductDto.getIsSubs();
        Long stock = createProductDto.getStock();
        String thumbImg = createProductDto.getThumbImg();
        Long discountRate = createProductDto.getDiscountRate();
        String productNo = createProductDto.getProductNo();
        Product product = Product.create(isOwn,name,price,isSubs,stock,thumbImg,discountRate,productNo);
        product.setCategory(categoryRepository.findById(createProductDto.getCategoryId()));

        return product;
    }

    public List<ProductImage> createProductImages(List<ProductImageDto> productImageDtoList, Product product){
        List<ProductImage> productImages = new ArrayList<>();

        for (ProductImageDto productImageDto: productImageDtoList) {
            // 이미지 저장
            Image image = Image.builder()
                    .url(productImageDto.getImgUrl())
                    .build();
            imageRepository.save(image);

            //ProductImage 엔티티 생성
            ProductImage productImage = ProductImage.create( product,
                    imageRepository.findById(image.getId()),
                    productImageDto.getSeqNo(),
                    productImageDto.getType());
            productImages.add(productImage);

            // 연관관계 추가
            product.addProductImages(productImage);
        }
        return productImages;
    }

    public List<ProdOption> createProdOptions(List<OptionDto> optionDtos, Product product){
        List<ProdOption> prodOptions = new ArrayList<>();
        // option category 조회
        for (int i = 0; i < optionDtos.size(); i++) {
            OptionCategory optionCategory = optionCategoryRepository.findByName(optionDtos.get(i).getOptionCategory());
            Option option = optionRepository.findByNameAndOptionCategoryId(optionDtos.get(i).getName(),optionCategory.getId());
            Long optionId = option.getId();
            // 옵션 카테고리 없는 경우 만들어주기
            if (optionCategory.getName().equals("EMPTY")){
                OptionCategory newCategory = createOptionCategory(optionDtos.get(i));
                optionId = createOption(optionDtos.get(i), newCategory);
            } else if (option.getName().equals("EMPTY")) {
                optionId = createOption(optionDtos.get(i),optionCategory);
            }

            ProdOption prodOption = ProdOption.builder()
                    .option(optionRepository.findById(optionId))
                    .build();

            prodOptions.add(prodOption);
            // 연관관계 추가
            product.addProdOptions(prodOption);

        }
        return prodOptions;
    }

    public List<ProdTag> createTags(List<ProdTagDto> prodTagDtos, Product product){
        List<ProdTag> prodTags = new ArrayList<>();

        for (int i = 0; i < prodTagDtos.size(); i++) {
            TagCategory tagCategory = tagCategoryRepository.findByName(prodTagDtos.get(i).getTagKey());
            Tag tag = tagRepository.findByName(prodTagDtos.get(i).getTagValue());
            Long tagId = tag.getId();
            // 태그 카테고리 없는 경우 만들어주기
            if (tagCategory.getName().equals("EMPTY")){
                TagCategory newCategory = createTagCategory(prodTagDtos.get(i));
                tagId = createTag(prodTagDtos.get(i), newCategory);
            } else if (tag.getName().equals("EMPTY")) {
                tagId = createTag(prodTagDtos.get(i),tagCategory);
            }

            ProdTag prodTag = ProdTag.builder()
                    .tag(tagRepository.findById(tagId))
                    .build();
            prodTags.add(prodTag);
            // 연관관계 추가
            product.addProdTags(prodTag);
        }
        return prodTags;
    }

    private OptionCategory createOptionCategory(OptionDto optionDto){
        OptionCategory newCategory = OptionCategory.builder()
                .name(optionDto.getOptionCategory())
                .build();
        optionCategoryRepository.save(newCategory);
        return newCategory;
    }

    private Long createOption(OptionDto optionDto,OptionCategory optionCategory){
        Option newOption = Option.builder()
                .optionCategory(optionCategory)
                .name(optionDto.getName())
                .build();
        optionRepository.save(newOption);
        // 연관관계 추가
        optionCategory.addOptions(newOption);

        return newOption.getId();
    }

    private TagCategory createTagCategory(ProdTagDto prodTagDto){
        TagCategory newCategory = TagCategory.builder()
                .name(prodTagDto.getTagKey())
                .build();
        tagCategoryRepository.save(newCategory);
        return newCategory;
    }

    private Long createTag(ProdTagDto prodTagDto,TagCategory tagCategory){
        Tag newTag = Tag.builder()
                .tagCategory(tagCategory)
                .name(prodTagDto.getTagValue())
                .build();
        tagRepository.save(newTag);
        // 연관관계 추가
        tagCategory.addTags(newTag);

        return newTag.getId();
    }


}
