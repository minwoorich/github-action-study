//package com.objects.marketbridge.product.service;
//
//import com.objects.marketbridge.category.domain.Category;
//import com.objects.marketbridge.product.domain.Product;
//import com.objects.marketbridge.product.service.port.ProductRepository;
//import com.objects.marketbridge.category.service.port.CategoryRepository;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Iterator;
//import java.util.List;
//
//@Service
//@Transactional(readOnly = true)
//@RequiredArgsConstructor
//public class BulkUploadProductService {
//
//    private final ProductRepository productRepository;
//    private final CategoryRepository categoryRepository;
//
//    //상품들 Excel파일로 벌크등록
//    @Transactional
//    public String uploadExcelFile(MultipartFile file) {
//
//        // 엑셀 파일 읽기 : Workbook workbook = new XSSFWorkbook(file.getInputStream())
//        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
//
//            // 첫 번째 시트 선택
//            Sheet sheet = workbook.getSheetAt(0);
//
//            Iterator<Row> rowIterator = sheet.iterator();
//
//            while (rowIterator.hasNext()) {
//
//                Row row = rowIterator.next();
//
//                // 첫 번째 행은 건너뛰기
//                if (row.getRowNum() == 0) {
//                    continue;
//                }
//
//                //이하의 로직은 카테고리가 대량등록되어 있다고 가정함.
//
//                // 필요한 열의 데이터 추출
//                String largeCategoryName = row.getCell(0).getStringCellValue(); // 대분류 카테고리 이름
//                String mediumCategoryName = row.getCell(1).getStringCellValue(); // 중분류 카테고리 이름
//                String smallCategoryName = row.getCell(2).getStringCellValue(); // 소분류 카테고리 이름
//                String productName = row.getCell(3).getStringCellValue(); // 상품 이름
//                String imgType = row.getCell(4).getStringCellValue(); // 상품 이미지 타입
//                Long imgSeqNo = (long) row.getCell(5).getNumericCellValue(); // 상품 이미지 시퀀스 번호
//                Long dcRate = (long) row.getCell(6).getNumericCellValue(); // 상품 할인률
//                Long price = (long) row.getCell(7).getNumericCellValue(); // 상품 가격
//                String tagsObj = row.getCell(8).getStringCellValue(); // tags_obj
//                String imgPath = row.getCell(9).getStringCellValue(); // img_path
//
//                //상품에 해당하는 카테고리 찾기.
////                Category largeCategory;
////                Category mediumCategory;
//                Category smallCategory = null;
//
//                List<Category> smallCategoriesToBeCompared
//                        = categoryRepository.findAllByNameAndLevel(smallCategoryName, 2L);
//                for (Category smallCategoryToBeCompared : smallCategoriesToBeCompared) {
//
//                    if (smallCategoriesToBeCompared.size() == 0) { //해당명칭의 소분류카테고리가 0개인경우
//                        throw new EntityNotFoundException("해당 소분류 카테고리가 없습니다.");
//
//                    } else if (smallCategoriesToBeCompared.size() == 1) { //해당명칭의 소분류카테고리가 1개인경우
//                        smallCategory = smallCategoryToBeCompared;
//                        break;
//
//                    } else { //해당명칭의 소분류카테고리가 2개이상인 경우 //중분류나 대분류에서 중복된 것임.
//
//                        List<Category> mediumCategoriesToBeCompared
//                                = categoryRepository.findAllByNameAndLevel(mediumCategoryName, 1L);
//
//                        for (Category mediumCategoryToBeCompared : mediumCategoriesToBeCompared) {
//                            if (mediumCategoryToBeCompared.getName().equals(mediumCategoryName) &&
//                                    categoryRepository.findById(mediumCategoryToBeCompared.getParentId()).getName()
//                                            .equals(largeCategoryName)) {
//                                smallCategory = smallCategoryToBeCompared;
////                                mediumCategory = mediumCategoryToBeCompared;
////                                largeCategory = categoryRepository.findById(mediumCategoryToBeCompared.getParentId());
//                                break;
//                            }
//                        }
//                    }
//                }
//                // 소분류 카테고리가 설정되어 있다면 상품 등록
//                if (smallCategory != null) {
//                    Product product = Product.builder()
//                            .category(smallCategory)
//                            .isOwn(false)
//                            .name(productName)
//                            .price(price)
//                            .isSubs(false)
//                            .stock(100L)
//                            .thumbImg(imgPath)
//                            .discountRate(dcRate)
//                            .build();
//                    productRepository.save(product);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "파일 업로드 및 처리 중 오류가 발생했습니다.";
//        }
//        return "파일 업로드 및 데이터베이스 저장이 완료되었습니다.";
//    } //uploadExcelFile
//}