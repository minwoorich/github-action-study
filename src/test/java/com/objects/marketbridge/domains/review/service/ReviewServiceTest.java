package com.objects.marketbridge.domains.review.service;

import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.service.port.OrderDetailCommandRepository;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import com.objects.marketbridge.domains.review.domain.SurveyCategory;
import com.objects.marketbridge.domains.review.domain.SurveyContent;
import com.objects.marketbridge.domains.review.dto.ReviewSurveyCategoryContentsDto;
import com.objects.marketbridge.domains.review.service.port.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
public class ReviewServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderDetailCommandRepository orderDetailCommandRepository;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ReviewLikeRepository reviewLikeRepository;
    @Autowired
    ReviewSurveyRepository reviewSurveyRepository;
    @Autowired
    SurveyCategoryRepository surveyCategoryRepository;
    @Autowired
    SurveyContentRepository surveyContentRepository;
    @Autowired
    OrderDetailReviewRepository orderDetailReviewRepository;

    @BeforeEach
    void init(){
        surveyCategoryRepository = Mockito.mock(SurveyCategoryRepository.class);
        surveyContentRepository = Mockito.mock(SurveyContentRepository.class);
        reviewService = new ReviewService(
                null, null, null,
                surveyCategoryRepository, surveyContentRepository,
                null, null
        );
    }

    @Test
    @DisplayName("리뷰서베이 선택창 조회")
    void getReviewSurveyCategoryContentsList(){
        //given
        Long productId = 1L;

        //Mock SurveyCategory, Mock SurveyContent 데이터 설정
//        surveyCategoryRepository = Mockito.mock(SurveyCategoryRepository.class);
//        surveyContentRepository = Mockito.mock(SurveyContentRepository.class);
//        reviewService = new ReviewService(
//                null, null, null,
//                surveyCategoryRepository, surveyContentRepository,
//                null, null
//        );

        // Mock SurveyCategory 객체 생성
        SurveyCategory surveyCategory1 = Mockito.mock(SurveyCategory.class);
        SurveyCategory surveyCategory2 = Mockito.mock(SurveyCategory.class);
        SurveyCategory surveyCategory3 = Mockito.mock(SurveyCategory.class);
        SurveyCategory surveyCategory4 = Mockito.mock(SurveyCategory.class);

        // getId() 메서드 stubbing
        Mockito.when(surveyCategory1.getId()).thenReturn(1L);
        Mockito.when(surveyCategory2.getId()).thenReturn(2L);
        Mockito.when(surveyCategory3.getId()).thenReturn(3L);
        Mockito.when(surveyCategory4.getId()).thenReturn(4L);

        // name() 메서드 stubbing
        Mockito.when(surveyCategory1.getName()).thenReturn("평소사이즈(테스트)");
        Mockito.when(surveyCategory2.getName()).thenReturn("키(테스트)");
        Mockito.when(surveyCategory3.getName()).thenReturn("색상(테스트)");
        Mockito.when(surveyCategory4.getName()).thenReturn("사이즈(테스트)");
        //Mock SurveyCategory, Mock SurveyContent 데이터 설정 끝

        SurveyContent surveyContent31 = SurveyContent.builder()
                .surveyCategoryId(surveyCategory3.getId())
                .content("화면보다옅어요(테스트)")
                .build();
        SurveyContent surveyContent32 = SurveyContent.builder()
                .surveyCategoryId(surveyCategory3.getId())
                .content("화면과같아요(테스트)")
                .build();
        SurveyContent surveyContent33 = SurveyContent.builder()
                .surveyCategoryId(surveyCategory3.getId())
                .content("화면보다진해요(테스트)")
                .build();
        SurveyContent surveyContent34 = SurveyContent.builder()
                .surveyCategoryId(surveyCategory3.getId())
                .content("화면과달라요(테스트)")
                .build();

        SurveyContent surveyContent41 = SurveyContent.builder()
                .surveyCategoryId(surveyCategory4.getId())
                .content("딱맞아요(테스트)")
                .build();
        SurveyContent surveyContent42 = SurveyContent.builder()
                .surveyCategoryId(surveyCategory4.getId())
                .content("보통이에요(테스트)")
                .build();
        SurveyContent surveyContent43 = SurveyContent.builder()
                .surveyCategoryId(surveyCategory4.getId())
                .content("너무커요(테스트)")
                .build();
        SurveyContent surveyContent44 = SurveyContent.builder()
                .surveyCategoryId(surveyCategory4.getId())
                .content("너무작아요(테스트)")
                .build();

        // Mock Repository 동작 설정
        Mockito.when(surveyCategoryRepository.findAllByProductId(productId)).thenReturn(
                Arrays.asList(
                        surveyCategory1, surveyCategory2, surveyCategory3, surveyCategory4));

        Mockito.when(surveyContentRepository.findAllBySurveyCategoryIdIn(
                Arrays.asList(
                        surveyCategory1.getId(), surveyCategory2.getId(),
                        surveyCategory3.getId(), surveyCategory4.getId())))
                .thenReturn(
                        Arrays.asList(surveyContent31, surveyContent32, surveyContent33, surveyContent34,
                                surveyContent41, surveyContent42, surveyContent43, surveyContent44));
        // Mock Repository 동작 설정 끝

        ReviewSurveyCategoryContentsDto reviewSurveyCategoryContentsDto1
                = ReviewSurveyCategoryContentsDto.builder()
                .category(surveyCategory1.getName())
                .contents(null)
                .build();

        ReviewSurveyCategoryContentsDto reviewSurveyCategoryContentsDto2
                = ReviewSurveyCategoryContentsDto.builder()
                .category(surveyCategory2.getName())
                .contents(null)
                .build();

        List<String> contents3 = Arrays.asList(
                surveyContent31.getContent(),
                surveyContent32.getContent(),
                surveyContent33.getContent(),
                surveyContent34.getContent());
        ReviewSurveyCategoryContentsDto reviewSurveyCategoryContentsDto3
                = ReviewSurveyCategoryContentsDto.builder()
                .category(surveyCategory3.getName())
                .contents(contents3)
                .build();

        List<String> contents4 = Arrays.asList(
                surveyContent41.getContent(),
                surveyContent42.getContent(),
                surveyContent43.getContent(),
                surveyContent44.getContent());
        ReviewSurveyCategoryContentsDto reviewSurveyCategoryContentsDto4
                = ReviewSurveyCategoryContentsDto.builder()
                .category(surveyCategory4.getName())
                .contents(contents4)
                .build();

        List<ReviewSurveyCategoryContentsDto> reviewSurveyCategoryContentsDtoList
                = Arrays.asList(
                        reviewSurveyCategoryContentsDto1,
                        reviewSurveyCategoryContentsDto2,
                        reviewSurveyCategoryContentsDto3,
                        reviewSurveyCategoryContentsDto4);

        // when
        List<ReviewSurveyCategoryContentsDto> response
                = reviewService.getReviewSurveyCategoryContentsList(productId);

        // then
        //surveyCategory
        Assertions.assertThat(response.get(0).getCategory())
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(0).getCategory());
        Assertions.assertThat(response.get(1).getCategory())
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(1).getCategory());
        Assertions.assertThat(response.get(2).getCategory())
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(2).getCategory());
        Assertions.assertThat(response.get(3).getCategory())
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(3).getCategory());

        //surveyContent
        Assertions.assertThat(response.get(0).getContents())
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(0).getContents());

        Assertions.assertThat(response.get(1).getContents())
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(1).getContents());

        Assertions.assertThat(response.get(2).getContents().get(0))
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(2).getContents().get(0));
        Assertions.assertThat(response.get(2).getContents().get(1))
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(2).getContents().get(1));
        Assertions.assertThat(response.get(2).getContents().get(2))
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(2).getContents().get(2));
        Assertions.assertThat(response.get(2).getContents().get(3))
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(2).getContents().get(3));

        Assertions.assertThat(response.get(3).getContents().get(0))
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(3).getContents().get(0));
        Assertions.assertThat(response.get(3).getContents().get(1))
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(3).getContents().get(1));
        Assertions.assertThat(response.get(3).getContents().get(2))
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(3).getContents().get(2));
        Assertions.assertThat(response.get(3).getContents().get(3))
                .isEqualTo(reviewSurveyCategoryContentsDtoList.get(3).getContents().get(3));
    }



//    @Test
//    @DisplayName("리뷰 등록")
//    void createReview() {
//
//        //given
//        Member testMember = Member.builder().build();
//        memberRepository.save(testMember);
//        Long testMemberId = testMember.getId();
//
//        Product testProduct = Product.builder().build();
//        productRepository.save(testProduct);
//        Long testProductId = testProduct.getId();
//
//        OrderDetail testOrderDetail = OrderDetail.builder()
//                .statusCode(StatusCodeType.DELIVERY_COMPLETED.toString())
//                .build();
//        orderDetailCommandRepository.save(testOrderDetail);
//        Long testOrderDetailId = testOrderDetail.getId();
//
//        List<String> testReviewImgUrls = new ArrayList<>();
//        testReviewImgUrls.add("0001.jpg");
//        testReviewImgUrls.add("0002.jpg");
//        testReviewImgUrls.add("0003.jpg");
//
//        CreateReviewDto testRequest = CreateReviewDto.builder()
//                .productId(testProductId)
//                .orderDetailId(testOrderDetailId)
//                .rating(5)
//                .content("리뷰내용")
//                .reviewImgUrls(testReviewImgUrls)
//                .build();
//
//        //when
//        Long testReviewId = reviewService.createReview(testRequest, testMemberId);
//        Review testReview = reviewRepository.findById(testReviewId);
//
//        //then
//        Assertions.assertThat(testRequest.getProductId()).isEqualTo(testReview.getProduct().getId());
//        Assertions.assertThat(testReview.getReviewImages().get(0).getImage().getUrl()).isEqualTo(testReviewImgUrls.get(0));
//    }
//
//
//
//
//    @Test
//    @DisplayName("리뷰를_조회하면_조회된다.")
//    void getReviewServiceTest() {
//
//        //given
//        Member testMember = Member.builder().build();
//        memberRepository.save(testMember);
//        Long testMemberId = testMember.getId();
//
//        Product testProduct = Product.builder().build();
//        productRepository.save(testProduct);
//        Long testProductId = testProduct.getId();
//
//        OrderDetail testOrderDetail = OrderDetail.builder()
//                .statusCode(StatusCodeType.DELIVERY_COMPLETED.toString())
//                .build();
//        orderDetailCommandRepository.save(testOrderDetail);
//        Long testOrderDetailId = testOrderDetail.getId();
//
//        List<String> testReviewImgUrls = new ArrayList<>();
//        testReviewImgUrls.add("0001.jpg");
//        testReviewImgUrls.add("0002.jpg");
//        testReviewImgUrls.add("0003.jpg");
//
//        CreateReviewDto testRequest = CreateReviewDto.builder()
//                .productId(testProductId)
//                .orderDetailId(testOrderDetailId)
//                .rating(5)
//                .content("리뷰내용")
//                .reviewImgUrls(testReviewImgUrls)
//                .build();
//
//        Long testReviewId = reviewService.createReview(testRequest, testMemberId);
//        Review testReview = reviewRepository.findById(testReviewId);
//
//        //when
//        ReviewAllValuesDto response = reviewService.getReview(testReviewId, testMemberId);
//
//        //then
//        Assertions.assertThat(testReview.getReviewImages().get(0).getImage().getUrl())
//                .isEqualTo(response.getReviewImgUrls().get(0));
//    }
//
//
//
//    @Test
//    @DisplayName("리뷰를_수정하면_수정된다.")
//    void updateReviewServiceTest() {
//
//        //given
//        Member testMember = Member.builder().build();
//        memberRepository.save(testMember);
//        Long testMemberId = testMember.getId();
//
//        Product testProduct = Product.builder().build();
//        productRepository.save(testProduct);
//        Long testProductId = testProduct.getId();
//
//        OrderDetail testOrderDetail = OrderDetail.builder()
//                .statusCode(StatusCodeType.DELIVERY_COMPLETED.toString())
//                .build();
//        orderDetailCommandRepository.save(testOrderDetail);
//        Long testOrderDetailId = testOrderDetail.getId();
//
//        List<String> testReviewImgUrls = new ArrayList<>();
//        testReviewImgUrls.add("0001.jpg");
//        testReviewImgUrls.add("0002.jpg");
//        testReviewImgUrls.add("0003.jpg");
//
//        CreateReviewDto testRequest = CreateReviewDto.builder()
//                .productId(testProductId)
//                .orderDetailId(testOrderDetailId)
//                .rating(5)
//                .content("리뷰내용")
//                .reviewImgUrls(testReviewImgUrls)
//                .build();
//
//        Long testReviewId = reviewService.createReview(testRequest, testMemberId);
//        Review testReview = reviewRepository.findById(testReviewId);
//
//        List<String> updatedReviewImgUrls = new ArrayList<>();
//        updatedReviewImgUrls.add("0004.jpg");
//        updatedReviewImgUrls.add("0005.jpg");
//        updatedReviewImgUrls.add("0006.jpg");
//        updatedReviewImgUrls.add("0007.jpg");
//        updatedReviewImgUrls.add("0008.jpg");
//
//        ReviewModifiableValuesDto testUpdateRequest = ReviewModifiableValuesDto.builder()
//                .rating(4)
//                .content("리뷰수정된내용")
//                .reviewImgUrls(updatedReviewImgUrls)
//                .build();
//
//        //when
//        ReviewIdDto resultDto = reviewService.updateReview(testUpdateRequest, testReviewId, testMemberId);
//
//        //then
//        Review result = reviewRepository.findById(resultDto.getReviewId());
//        Assertions.assertThat(result.getRating()).isEqualTo(4);
//        Assertions.assertThat(result.getContent()).isEqualTo("리뷰수정된내용");
//        Assertions.assertThat(result.getReviewImages().get(0).getImage().getUrl()).isEqualTo(updatedReviewImgUrls.get(0));
//    }
//
//
//
//    @Test
//    @DisplayName("리뷰를_삭제하면_삭제된다.")
//    void deleteReviewServiceTest(){
//
//        //given
//        Member testMember = Member.builder().build();
//        memberRepository.save(testMember);
//        Long testMemberId = testMember.getId();
//
//        Product testProduct = Product.builder().build();
//        productRepository.save(testProduct);
//        Long testProductId = testProduct.getId();
//
//        OrderDetail testOrderDetail = OrderDetail.builder()
//                .statusCode(StatusCodeType.DELIVERY_COMPLETED.toString())
//                .build();
//        orderDetailCommandRepository.save(testOrderDetail);
//        Long testOrderDetailId = testOrderDetail.getId();
//
//        List<String> testReviewImgUrls = new ArrayList<>();
//        testReviewImgUrls.add("0001.jpg");
//        testReviewImgUrls.add("0002.jpg");
//        testReviewImgUrls.add("0003.jpg");
//
//        CreateReviewDto testRequest = CreateReviewDto.builder()
//                .productId(testProductId)
//                .orderDetailId(testOrderDetailId)
//                .rating(5)
//                .content("리뷰내용")
//                .reviewImgUrls(testReviewImgUrls)
//                .build();
//
//        Long testReviewId = reviewService.createReview(testRequest, testMemberId);
//        Review testReview = reviewRepository.findById(testReviewId);
//
//        //when
//        reviewService.deleteReview(testReviewId, testMemberId);
//
//        //then
//        Assertions.assertThatThrownBy(() -> reviewRepository.findById(testReviewId))
//                .isInstanceOf(JpaObjectRetrievalFailureException.class);
//    }
}
