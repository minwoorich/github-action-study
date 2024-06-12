package com.objects.marketbridge.domains.product.controller;

import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.common.security.annotation.UserAuthorize;
import com.objects.marketbridge.domains.product.controller.request.CreateProductRequestDto;
import com.objects.marketbridge.domains.product.dto.ProductDetailDto;
import com.objects.marketbridge.domains.product.dto.ProductSimpleDto;
import com.objects.marketbridge.domains.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

////    private final BulkUploadProductService bulkUploadProductService;
    private final ProductService productService;
//
//
//    //상품들 Excel파일로 대량등록
////    @UserAuthorize
////    @PostMapping("/uploadExcel")
////    public String uploadExcelFile(@RequestParam("file") MultipartFile file){
////        return bulkUploadProductService.uploadExcelFile(file);
////    }
//
//
    //상품등록
    @UserAuthorize
    @PostMapping()
    public ApiResponse<Long> createProduct(@Valid @RequestBody CreateProductRequestDto request) {
        Long productId = productService.create(request);
        return ApiResponse.ok(productId);
    }
//
//
    //상품 카테고리별 조회
    @UserAuthorize
    @GetMapping()
    public ApiResponse<Page<ProductSimpleDto>> getProductByCategory(@PageableDefault(page = 1, size = 60, sort = "created_at", direction = Sort.Direction.DESC)  Pageable pageable
            , @RequestParam("categoryCode") String categoryId){
        Page<ProductSimpleDto> productPage = productService.getProductByCategory(pageable,categoryId);
        return ApiResponse.ok(productPage);
    }

    // 상품 상세 정보 조회
    @UserAuthorize
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailDto> getProductDetail (@PathVariable("id") Long id){
        ProductDetailDto result = productService.getProductDetail(id);
        return ApiResponse.ok(result);
    }

}
