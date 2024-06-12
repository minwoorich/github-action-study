package com.objects.marketbridge.domains.category.controller;

import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.common.security.annotation.UserAuthorize;
import com.objects.marketbridge.domains.category.dto.CategoryDto;
import com.objects.marketbridge.domains.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private final CategoryService categoryService;
//    private final BulkUploadCategoryService bulkUploadCategoryService;


//    //벌크 등록
//    @UserAuthorize
//    @PostMapping("/categories/uploadExcel")
//    public String uploadExcelFile(@RequestParam("file") MultipartFile file){
//        return bulkUploadCategoryService.uploadExcelFile(file);
//    }


    //조회
    //전체(1depth,2depth,3depth). 1depth가 하위의 2depth 전부를, 2depth 카테고리가 하위의 3depth 전부를 포함하는 형태로 JSON형식.
    @UserAuthorize
    @GetMapping("/total")
    public ApiResponse<List<CategoryDto>> getTotalCategories() {
        return ApiResponse.ok(categoryService.getTotalCategories());
    }

    // 하위 카테고리 전체 조회
    @UserAuthorize
    @GetMapping()
    public ApiResponse<List<CategoryDto>> getLowerCategories(@RequestParam("categoryId") Long categoryId) {
        return ApiResponse.ok(categoryService.getLowerCategories(categoryId));
    }
}
