package com.objects.marketbridge.domains.review.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.security.annotation.WithMockCustomUser;
import com.objects.marketbridge.common.security.config.SpringSecurityTestConfig;
import com.objects.marketbridge.domains.review.dto.*;
import com.objects.marketbridge.domains.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {SpringSecurityTestConfig.class})
@ExtendWith(RestDocumentationExtension.class)
public class ReviewControllerTest {

    @MockBean
    ReviewService reviewService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentationContextProvider))
                .build();
    }




    @Test
    @WithMockCustomUser
    @DisplayName("리뷰서베이 선택창 조회")
    public void getReviewSurveyCategoryContentsList() throws Exception {
        //given
        Long productId = 1L;
        List<ReviewSurveyCategoryContentsDto> reviewSurveyCategoryContentsDtos;

        List<String> contents1 = new ArrayList<>();
        ReviewSurveyCategoryContentsDto reviewSurveyCategoryContentsDto1
                = ReviewSurveyCategoryContentsDto.builder()
                .category("평소사이즈")
                .contents(contents1)
                .build();

        List<String> contents2 = new ArrayList<>();
        ReviewSurveyCategoryContentsDto reviewSurveyCategoryContentsDto2
                = ReviewSurveyCategoryContentsDto.builder()
                .category("키")
                .contents(contents2)
                .build();

        List<String> contents3 = Arrays.asList("화면보다 옅어요", "화면과같아요", "화면보다진해요", "화면과달라요");
        ReviewSurveyCategoryContentsDto reviewSurveyCategoryContentsDto3
                = ReviewSurveyCategoryContentsDto.builder()
                .category("색상")
                .contents(contents3)
                .build();

        List<String> contents4 = Arrays.asList("딱맞아요", "보통이예요", "너무커요", "너무작아요");
        ReviewSurveyCategoryContentsDto reviewSurveyCategoryContentsDto4
                = ReviewSurveyCategoryContentsDto.builder()
                .category("사이즈")
                .contents(contents4)
                .build();

        reviewSurveyCategoryContentsDtos
                = Arrays.asList(
                reviewSurveyCategoryContentsDto1,
                reviewSurveyCategoryContentsDto2,
                reviewSurveyCategoryContentsDto3,
                reviewSurveyCategoryContentsDto4);

        given(reviewService.getReviewSurveyCategoryContentsList(productId))
                .willReturn(reviewSurveyCategoryContentsDtos);

        //when //then
        mockMvc.perform(get("/review/surveys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .param("productId", String.valueOf(productId)))
                .andExpect(status().isOk())
                .andDo(document("review-get-surveys",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                resource(ResourceSnippetParameters.builder()
                                        .queryParameters(
                                                parameterWithName("productId").description("상품 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
//                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답데이터(리뷰서베이 질문과 선택지들)"),
                                                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("응답데이터(리뷰서베이 질문과 선택지들)"),
                                                fieldWithPath("data[].category").type(JsonFieldType.STRING).description("리뷰서베이 질문(카테고리)"),
//                                        fieldWithPath("data[0].contents").type(JsonFieldType.ARRAY).optional().description("리뷰서베이 선택지들(컨텐트들) (빈배열이거나 null인경우 직접 입력)"),
                                                fieldWithPath("data[].contents[]").type(JsonFieldType.ARRAY).optional().description("리뷰서베이 선택지들(컨텐트들) (빈배열이거나 null인경우 직접 입력)")
                                        )
                                        .responseSchema(Schema.schema("GetReviewSurveysRes"))
                                        .build()))
                );
    }




    @Test
    @WithMockCustomUser
    @DisplayName("리뷰 등록")
    public void createReview() throws Exception {
        //given
        // 리뷰 생성에 필요한 요청 데이터
        CreateReviewDto request = getCreateReviewDto();
        willDoNothing().given(reviewService).createReview(any(CreateReviewDto.class), anyLong());

        //when //then
        // API 요청 및 응답 문서화
        String jsonContent = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")) // 액세스 토큰
                .andExpect(status().isCreated())
                .andDo(document("review-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .requestFields(
                                        fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                                        fieldWithPath("rating").type(JsonFieldType.NUMBER).description("별점"),
                                        fieldWithPath("summary").type(JsonFieldType.STRING).description("한줄요약"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰내용"),
                                        fieldWithPath("reviewSurveys").description("리뷰서베이 데이터 리스트(카테고리, 선택 또는 입력된 내용 의 데이터 리스트)")
                                                .type(JsonFieldType.ARRAY).optional(), // 필드가 선택적인 경우 optional()을 사용
                                        fieldWithPath("reviewSurveys[].surveyCategoryId").type(JsonFieldType.NUMBER).description("서베이 카테고리 아이디"),
                                        fieldWithPath("reviewSurveys[].surveyCategoryName").type(JsonFieldType.STRING).description("서베이 카테고리 이름"),
                                        fieldWithPath("reviewSurveys[].content").type(JsonFieldType.STRING).description("선택하거나 입력한 내용"),
                                        fieldWithPath("reviewImages").description("리뷰 이미지 URL 목록").type(JsonFieldType.ARRAY).optional(),
                                        fieldWithPath("reviewImages[].seqNo").type(JsonFieldType.NUMBER).description("이미지 순번"),
                                        fieldWithPath("reviewImages[].imgUrl").type(JsonFieldType.STRING).description("이미지 주소"),
                                        fieldWithPath("reviewImages[].description").type(JsonFieldType.STRING).description("이미지 설명")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
                                )
                                .requestSchema(Schema.schema("PostReviewReq"))
                                .responseSchema(Schema.schema("PostReviewRes"))
                                .build()))
                );
    }

    private CreateReviewDto getCreateReviewDto() {
        List<CreateReviewSurveyDto> reviewSurveys = new ArrayList<>();
        CreateReviewSurveyDto reviewSurvey1 = CreateReviewSurveyDto.builder()
                .surveyCategoryId(1L)
                .surveyCategoryName("키")
                .content("(직접입력)163")
                .build();
        CreateReviewSurveyDto reviewSurvey2 = CreateReviewSurveyDto.builder()
                .surveyCategoryId(2L)
                .surveyCategoryName("평소사이즈")
                .content("(직접입력)XS")
                .build();
        CreateReviewSurveyDto reviewSurvey3 = CreateReviewSurveyDto.builder()
                .surveyCategoryId(3L)
                .surveyCategoryName("색상")
                .content("화면과같아요")
                .build();
        CreateReviewSurveyDto reviewSurvey4 = CreateReviewSurveyDto.builder()
                .surveyCategoryId(4L)
                .surveyCategoryName("사이즈")
                .content("딱맞아요")
                .build();
        reviewSurveys.add(reviewSurvey1);
        reviewSurveys.add(reviewSurvey2);
        reviewSurveys.add(reviewSurvey3);
        reviewSurveys.add(reviewSurvey4);

        List<ReviewImageDto> reviewImgUrls = getReviewImageDtos();

        return CreateReviewDto.builder()
                .productId(1L)
                .rating(5)
                .reviewSurveys(reviewSurveys)
                .content("리뷰내용")
                .reviewImages(reviewImgUrls)
                .summary("한줄요약")
                .build();
    }

    private List<ReviewImageDto> getReviewImageDtos() {
        List<ReviewImageDto> reviewImgUrls = new ArrayList<>();
        ReviewImageDto image1 = ReviewImageDto.builder().seqNo(1L).imgUrl("001.jpg").description("캡션1").build();
        ReviewImageDto image2 = ReviewImageDto.builder().seqNo(2L).imgUrl("002.jpg").description("캡션2").build();
        ReviewImageDto image3 = ReviewImageDto.builder().seqNo(3L).imgUrl("003.jpg").description("캡션3").build();

        reviewImgUrls.add(image1);
        reviewImgUrls.add(image2);
        reviewImgUrls.add(image3);
        return reviewImgUrls;
    }




    @Test
    @WithMockCustomUser
    @DisplayName("리뷰 수정")
    public void updateReview() throws Exception {
        //given
        UpdateReviewDto updateReviewDto = getUpdateReviewDto();
        // 리뷰 수정 서비스 메서드의 반환값을 목 객체로 설정
        willDoNothing().given(reviewService).updateReview(any(UpdateReviewDto.class));

        //when //then
        mockMvc.perform(patch("/review") // PATCH 요청으로 수정
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(updateReviewDto))) // 요청 바디에 수정 정보를 포함
                .andExpect(status().isOk())
                .andDo(document("review-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .requestFields(
                                        fieldWithPath("reviewId").type(JsonFieldType.NUMBER).description("수정 요청한 리뷰아이디"),
                                        fieldWithPath("rating").type(JsonFieldType.NUMBER).description("수정한 별점"),
                                        fieldWithPath("summary").type(JsonFieldType.STRING).description("수정한 한줄요약"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("수정한 리뷰내용"),
                                        fieldWithPath("updateReviewSurveys").type(JsonFieldType.ARRAY).optional().description("리뷰서베이 데이터 리스트(카테고리, 선택 또는 입력된 내용 의 데이터 리스트)")
                                        , // 필드가 선택적인 경우 optional()을 사용
                                        fieldWithPath("updateReviewSurveys[].reviewSurveyId").type(JsonFieldType.NUMBER).description("리뷰서베이 아이디(리뷰 조회때 아이디 제공)"),
                                        fieldWithPath("updateReviewSurveys[].content").type(JsonFieldType.STRING).description("선택을 변경하거나 입력을 수정한 내용"),
                                        fieldWithPath("reviewImages").type(JsonFieldType.ARRAY).description("리뷰 이미지 URL 목록 - 이 목록에 없는 이미지는 데이터베이스에서 삭제됨.").type(JsonFieldType.ARRAY).optional(),
                                        fieldWithPath("reviewImages[].seqNo").type(JsonFieldType.NUMBER).description("이미지 순번"),
                                        fieldWithPath("reviewImages[].imgUrl").type(JsonFieldType.STRING).description("이미지 주소"),
                                        fieldWithPath("reviewImages[].description").type(JsonFieldType.STRING).description("이미지 설명")
                                )
                                .responseFields( // 응답에 대한 문서화
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
                                )
                                .requestSchema(Schema.schema("PatchReviewReq"))
                                .responseSchema(Schema.schema("PatchReviewRes"))
                                .build()))
                );
    }

    private UpdateReviewDto getUpdateReviewDto() {
        List<UpdateReviewSurveyDto> updateReviewSurveys = new ArrayList<>();
        UpdateReviewSurveyDto reviewSurvey1 = UpdateReviewSurveyDto.builder()
                .reviewSurveyId(1L)
                .content("163")
                .build();
        UpdateReviewSurveyDto reviewSurvey2 = UpdateReviewSurveyDto.builder()
                .reviewSurveyId(2L)
                .content("-")
                .build();
        UpdateReviewSurveyDto reviewSurvey3 = UpdateReviewSurveyDto.builder()
                .reviewSurveyId(3L)
                .content("화면과같아요")
                .build();
        UpdateReviewSurveyDto reviewSurvey4 = UpdateReviewSurveyDto.builder()
                .reviewSurveyId(4L)
                .content("딱맞아요")
                .build();
        updateReviewSurveys.add(reviewSurvey1);
        updateReviewSurveys.add(reviewSurvey2);
        updateReviewSurveys.add(reviewSurvey3);
        updateReviewSurveys.add(reviewSurvey4);

        List<ReviewImageDto> reviewImgUrls = getReviewImageDtos();

        return UpdateReviewDto.builder()
                .reviewId(1L)
                .rating(5)
                .updateReviewSurveys(updateReviewSurveys)
                .content("수정한 리뷰내용")
                .reviewImages(reviewImgUrls)
                .summary("수정한 한줄요약")
                .build();

    }




    @Test
    @WithMockCustomUser
    @DisplayName("리뷰 삭제")
    public void deleteReview() throws Exception {
        //given
        Long reviewId = 1L;

        willDoNothing().given(reviewService).deleteReview(reviewId);
        //when //then
        mockMvc.perform(delete("/review/{reviewId}", reviewId)
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("review-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("reviewId").description("삭제할 리뷰의 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
                                )
                                .responseSchema(Schema.schema("DeleteReviewRes"))
                                .build()))
                );
    }




    @Test
    @WithMockCustomUser
    @DisplayName("리뷰라이크 업서트")
    public void upsertReviewLike() throws Exception {
        //given
        Long reviewId = 1L;
//        Long memberId = 123L;

        // upsertReviewLike 메서드가 void를 반환하므로 willDoNothing을 사용하여 설정
        willDoNothing().given(reviewService).upsertReviewLike(anyLong(), anyLong());
        //when
        mockMvc.perform(post("/review/{reviewId}/like", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "bearer AccessToken"))
                //then
                .andExpect(status().isOk())
                .andDo(document("review-upsert-review-like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("reviewId").description("리뷰 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
                                )
                                .responseSchema(Schema.schema("PostReviewReviewIdLikeRes"))
                                .build()))
                );
    }




    @Test
    @WithMockCustomUser
    @DisplayName("리뷰의 좋아요 총갯수 카운트")
    public void countReviewLike() throws Exception {
        // given
        Long reviewId = 1L;
        ReviewLikeCountDto reviewLikeCountDto = ReviewLikeCountDto.builder()
                .count(10L) // 임의의 좋아요 수
                .build();
        given(reviewService.countReviewLike(reviewId)).willReturn(reviewLikeCountDto);

        // when // then
        mockMvc.perform(get("/review/{reviewId}/likes/count", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "bearer AccessToken"))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.reviewId").value(reviewId))
//                .andExpect(jsonPath("$.data.count").value(10L))
                .andDo(document("review-count-review-likes",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("reviewId").description("리뷰 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                        fieldWithPath("data.count").type(JsonFieldType.NUMBER).description("좋아요 수")
                                )
                                .responseSchema(Schema.schema("GetReviewReviewIdLikesCountRes"))
                                .build()))
                );
    }





    @Test
    @WithMockCustomUser
    @DisplayName("멤버의 리뷰미작성 주문상세들 총갯수 조회")
    public void getMemberReviewCountUnwritten() throws Exception {
        //given
        Long memberId = 1L;
        ReviewCountDto reviewCountDto = ReviewCountDto.builder().count(0L).build();
        given(reviewService.getMemberReviewCountUnwritten(memberId)).willReturn(reviewCountDto);

        //when //then
        mockMvc.perform(get("/reviews/members/{memberId}/unwritten/count", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("reviews-count-of-member-unwritten",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("memberId").description("멤버 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                        fieldWithPath("data.count").type(JsonFieldType.NUMBER).description("작성할 리뷰 총 갯수 (미작성 총갯수)")
                                )
                                .responseSchema(Schema.schema("GetReviewsMembersMemberIdUnwrittenCountRes"))
                                .build()))
                );
    }




    @Test
    @WithMockCustomUser
    @DisplayName("멤버의 리뷰 총갯수 조회")
    public void getMemberReviewCount() throws Exception {
        //given
        Long memberId = 1L;
        ReviewCountDto reviewCountDto = ReviewCountDto.builder().count(0L).build();
        given(reviewService.getMemberReviewCount(memberId)).willReturn(reviewCountDto);

        //when //then
        mockMvc.perform(get("/reviews/member/{memberId}/count", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("reviews-count-of-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("memberId").description("멤버 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                        fieldWithPath("data.count").type(JsonFieldType.NUMBER).description("리뷰 총 갯수")
                                )
                                .responseSchema(Schema.schema("GetReviewsMemberMemberIdCountRes"))
                                .build()))
                );
    }




    @Test
    @DisplayName("상품의 리뷰 총갯수 조회")
    public void getProductReviewCount() throws Exception {
        //given
        Long productId = 1L;
        ReviewCountDto reviewCountDto = ReviewCountDto.builder().count(productId).build();
        given(reviewService.getProductReviewCount(productId)).willReturn(reviewCountDto);

        //when //then
        mockMvc.perform(get("/reviews/product/{productId}/count", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("reviews-count-of-product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("productId").description("상품 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                        fieldWithPath("data.count").type(JsonFieldType.NUMBER).description("리뷰 총 갯수")
                                )
                                .responseSchema(Schema.schema("GetReviewsProductProductIdCountRes"))
                                .build()))
                );
    }




    @Test
    @WithMockCustomUser
    @DisplayName("리뷰 단건 조회")
    public void getReviewControllerTest() throws Exception {
        //given
        Long reviewId = 1L;

        List<ReviewImageDto> reviewImageDtos = new ArrayList<>();
        ReviewImageDto reviewImageDto1 = ReviewImageDto.builder()
                .seqNo(1L)
                .imgUrl("001.jpg")
                .description("캡션1")
                .build();
        ReviewImageDto reviewImageDto2 = ReviewImageDto.builder()
                .seqNo(2L)
                .imgUrl("002.jpg")
                .description("캡션2")
                .build();
        ReviewImageDto reviewImageDto3 = ReviewImageDto.builder()
                .seqNo(3L)
                .imgUrl("003.jpg")
                .description("캡션3")
                .build();
        reviewImageDtos.add(reviewImageDto1);
        reviewImageDtos.add(reviewImageDto2);
        reviewImageDtos.add(reviewImageDto3);

        List<GetReviewSurveyDto> getReviewSurveyDtos = new ArrayList<>();
        GetReviewSurveyDto reviewSurveyDto1 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리1(질문1)")
                .content("작성한 내용1")
                .build();
        GetReviewSurveyDto reviewSurveyDto2 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리2(질문2)")
                .content("작성한 내용2")
                .build();
        GetReviewSurveyDto reviewSurveyDto3 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리2(질문3)")
                .content("선택한 내용3")
                .build();
        GetReviewSurveyDto reviewSurveyDto4 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리4(질문4)")
                .content("선택한 내용4")
                .build();
        getReviewSurveyDtos.add(reviewSurveyDto1);
        getReviewSurveyDtos.add(reviewSurveyDto2);
        getReviewSurveyDtos.add(reviewSurveyDto3);
        getReviewSurveyDtos.add(reviewSurveyDto4);

        GetReviewDto getReviewDto = GetReviewDto.builder()
                .productThumbnailUrl("thumbnail.jpg")
                .memberName("멤버명")
                .rating(5)
                .productName("상품명")
                .summary("한줄요약")
                .reviewImageDtos(reviewImageDtos)
                .content("리뷰내용")
                .getReviewSurveyDtos(getReviewSurveyDtos)
                .build();

        given(reviewService.getReview(anyLong())).willReturn(getReviewDto);

        //when //then
        mockMvc.perform(get("/review/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "bearer AccessToken")) // 액세스 토큰
                .andExpect(status().isOk())
                .andDo(document("review-get-single-review",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("reviewId").description("리뷰 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                        fieldWithPath("data.productThumbnailUrl").type(JsonFieldType.STRING).description("상품 썸네일 이미지"),
                                        fieldWithPath("data.memberName").type(JsonFieldType.STRING).description("멤버명"),
                                        fieldWithPath("data.rating").type(JsonFieldType.NUMBER).description("별점"),
                                        fieldWithPath("data.productName").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data.summary").type(JsonFieldType.STRING).description("한줄요약"),
                                        fieldWithPath("data.reviewImageDtos").type(JsonFieldType.ARRAY).optional().description("이미지DTO리스트)")
                                        , // 필드가 선택적인 경우 optional()을 사용
                                        fieldWithPath("data.reviewImageDtos[].seqNo").type(JsonFieldType.NUMBER).description("이미지DTO리스트 시퀀스넘버"),
                                        fieldWithPath("data.reviewImageDtos[].imgUrl").type(JsonFieldType.STRING).description("이미지DTO리스트 이미지URL"),
                                        fieldWithPath("data.reviewImageDtos[].description").type(JsonFieldType.STRING).description("이미지DTO리스트 캡션(이미지설명)"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("리뷰내용"),
                                        fieldWithPath("data.getReviewSurveyDtos").type(JsonFieldType.ARRAY).optional().description("작성된 리뷰서베이리스트")
                                        , // 필드가 선택적인 경우 optional()을 사용
                                        fieldWithPath("data.getReviewSurveyDtos[].surveyCategoryName").type(JsonFieldType.STRING).description("서베이카테고리명"),
                                        fieldWithPath("data.getReviewSurveyDtos[].content").type(JsonFieldType.STRING).description("서베이내용(입력또는선택된)")
                                )
                                .responseSchema(Schema.schema("GetReviewReviewIdRes"))
                                .build()))
                );
    }




    @Test
    @WithMockCustomUser
    @DisplayName("멤버의 리뷰미작성 주문상세들 조회")
    public void getReviewable() throws Exception {
        // given
        Long memberId = 1L;

        ReviewableDto reviewableDto = ReviewableDto.builder()
                .productThumbnailUrl("001.jpg")
                .productName("상품명1")
                .deliveredDate(LocalDateTime.of(2024, 1, 11, 13, 15, 30,123456700))
                .build();
        ReviewableDto reviewableDto2 = ReviewableDto.builder()
                .productThumbnailUrl("002.jpg")
                .productName("상품명2")
                .deliveredDate(LocalDateTime.of(2024, 1, 12, 13, 15, 30,123456700))
                .build();
        ReviewableDto reviewableDto3 = ReviewableDto.builder()
                .productThumbnailUrl("003.jpg")
                .productName("상품명3")
                .deliveredDate(LocalDateTime.of(2024, 1, 13, 13, 15, 30,123456700))
                .build();

        // page 설정
        int page = 0;
        Pageable pageable = PageRequest.of(page, 5);

        // 실제 서비스 메서드 호출 결과를 설정
//        Page<ReviewableDto> reviewableDtoPage
//                = new PageImpl<>(Collections.singletonList(reviewableDto), pageable, 1);
//        given(reviewService.getReviewable(memberId, pageable)).willReturn(reviewableDtoPage);
        //3개의 Dto일때
        List<ReviewableDto> reviewableDtos = Arrays.asList(reviewableDto, reviewableDto2, reviewableDto3);
        Page<ReviewableDto> reviewableDtoPage
                = new PageImpl<>(reviewableDtos, pageable, reviewableDtos.size());
        given(reviewService.getReviewable(memberId, pageable)).willReturn(reviewableDtoPage);

        // when // then
        mockMvc.perform(get("/reviews/member/{memberId}/unwritten", memberId)
                        .param("page", String.valueOf(page))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "bearer AccessToken"))
                .andExpect(status().isOk())
                .andDo(document("reviews-of-member-unwritten",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("memberId").description("멤버 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                        fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("상품상세"),
                                        fieldWithPath("data.content[].productThumbnailUrl").type(JsonFieldType.STRING).description("상품이미지URL"),
                                        fieldWithPath("data.content[].productName").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data.content[].deliveredDate").type(JsonFieldType.STRING).description("배송일"),
                                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("페이지 정렬 정보"),
                                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("페이지 오프셋"),
                                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 되지 않았는지 여부"),
                                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("페이지 정렬 정보"),
                                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("페이지 정렬 여부"),
                                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("페이지 정렬되었는지 여부"),
                                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("페이지 정렬되지 않았는지 여부"),
                                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬되었는지 여부"),
                                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않았는지 여부"),
                                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("총 요소 수"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
                                )
                                .responseSchema(Schema.schema("GetReviewsMemberMemberIdUnwrittenRes"))
                                .build()))
                );
    }




    @Test
    @WithMockCustomUser
    @DisplayName("멤버의 모든 리뷰 조회")
    public void getReviewsOfMember() throws Exception {
        // given
        Long memberId = 1L;

        List<ReviewImageDto> reviewImageDtos = new ArrayList<>();
        ReviewImageDto reviewImageDto1 = ReviewImageDto.builder()
                .seqNo(1L)
                .imgUrl("101.jpg")
                .description("캡션1")
                .build();
        ReviewImageDto reviewImageDto2 = ReviewImageDto.builder()
                .seqNo(2L)
                .imgUrl("102.jpg")
                .description("캡션2")
                .build();
        ReviewImageDto reviewImageDto3 = ReviewImageDto.builder()
                .seqNo(3L)
                .imgUrl("103.jpg")
                .description("캡션3")
                .build();
        reviewImageDtos.add(reviewImageDto1);
        reviewImageDtos.add(reviewImageDto2);
        reviewImageDtos.add(reviewImageDto3);

        List<GetReviewSurveyDto> getReviewSurveyDtos = new ArrayList<>();
        GetReviewSurveyDto reviewSurveyDto1 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리1(질문1)")
                .content("작성한 내용1")
                .build();
        GetReviewSurveyDto reviewSurveyDto2 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리2(질문2)")
                .content("작성한 내용2")
                .build();
        GetReviewSurveyDto reviewSurveyDto3 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리2(질문3)")
                .content("선택한 내용3")
                .build();
        GetReviewSurveyDto reviewSurveyDto4 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리4(질문4)")
                .content("선택한 내용4")
                .build();
        getReviewSurveyDtos.add(reviewSurveyDto1);
        getReviewSurveyDtos.add(reviewSurveyDto2);
        getReviewSurveyDtos.add(reviewSurveyDto3);
        getReviewSurveyDtos.add(reviewSurveyDto4);

        GetReviewDto getReviewDto = GetReviewDto.builder()
                .productThumbnailUrl("thumbnail0001.jpg")
                .memberName("멤버명")
                .rating(5)
                .productName("상품명")
                .summary("한줄요약")
                .reviewImageDtos(reviewImageDtos)
                .content("리뷰내용")
                .getReviewSurveyDtos(getReviewSurveyDtos)
                .build();

        //Dto2
        List<ReviewImageDto> reviewImageDtos2 = new ArrayList<>();
        ReviewImageDto reviewImageDto21 = ReviewImageDto.builder()
                .seqNo(1L)
                .imgUrl("201.jpg")
                .description("캡션1")
                .build();
        ReviewImageDto reviewImageDto22 = ReviewImageDto.builder()
                .seqNo(2L)
                .imgUrl("202.jpg")
                .description("캡션2")
                .build();
        ReviewImageDto reviewImageDto23 = ReviewImageDto.builder()
                .seqNo(3L)
                .imgUrl("203.jpg")
                .description("캡션3")
                .build();
        reviewImageDtos2.add(reviewImageDto21);
        reviewImageDtos2.add(reviewImageDto22);
        reviewImageDtos2.add(reviewImageDto23);

        List<GetReviewSurveyDto> getReviewSurveyDtos2 = new ArrayList<>();
        GetReviewSurveyDto reviewSurveyDto21 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리1(질문1)")
                .content("작성한 내용1")
                .build();
        GetReviewSurveyDto reviewSurveyDto22 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리2(질문2)")
                .content("작성한 내용2")
                .build();
        GetReviewSurveyDto reviewSurveyDto23 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리2(질문3)")
                .content("선택한 내용3")
                .build();
        GetReviewSurveyDto reviewSurveyDto24 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리4(질문4)")
                .content("선택한 내용4")
                .build();
        getReviewSurveyDtos2.add(reviewSurveyDto21);
        getReviewSurveyDtos2.add(reviewSurveyDto22);
        getReviewSurveyDtos2.add(reviewSurveyDto23);
        getReviewSurveyDtos2.add(reviewSurveyDto24);

        GetReviewDto getReviewDto2 = GetReviewDto.builder()
                .productThumbnailUrl("thumbnail0002.jpg")
                .memberName("멤버명")
                .rating(5)
                .productName("상품명")
                .summary("한줄요약")
                .reviewImageDtos(reviewImageDtos2)
                .content("리뷰내용")
                .getReviewSurveyDtos(getReviewSurveyDtos2)
                .build();

        // page 및 sortBy 설정
        // 정렬 선택: createdAt 또는 likes
        int page = 0;
        String sortBy = "createdAt";
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sortBy).descending());

        // 실제 서비스 메서드 호출 결과를 설정
//        Page<GetReviewDto> reviewDtoPage
//                = new PageImpl<>(Collections.singletonList(getReviewDto), pageable, 1);
//        given(reviewService.getReviewsOfMember(memberId, pageable, sortBy)).willReturn(reviewDtoPage);
        //Dto가 2개일때
        List<GetReviewDto> getReviewDtoList = Arrays.asList(getReviewDto, getReviewDto2);
        Page<GetReviewDto> reviewDtoPage
                = new PageImpl<>(getReviewDtoList, pageable, getReviewDtoList.size());
        given(reviewService.getReviewsOfMember(memberId, pageable, sortBy)).willReturn(reviewDtoPage);

        // when // then
        mockMvc.perform(get("/reviews/member/{memberId}", memberId)
                        .param("page", String.valueOf(page))
                        .param("sortBy", sortBy)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "bearer AccessToken"))
                .andExpect(status().isOk())
                .andDo(document("reviews-of-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("memberId").description("멤버 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                        fieldWithPath("data.content[].productThumbnailUrl").type(JsonFieldType.STRING).description("상품 썸네일 이미지"),
                                        fieldWithPath("data.content[].memberName").type(JsonFieldType.STRING).description("멤버 이름"),
                                        fieldWithPath("data.content[].rating").type(JsonFieldType.NUMBER).description("별점"),
                                        fieldWithPath("data.content[].productName").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data.content[].summary").type(JsonFieldType.STRING).description("한줄요약"),
                                        fieldWithPath("data.content[].reviewImageDtos").type(JsonFieldType.ARRAY).description("리뷰이미지DTO리스트"),
                                        fieldWithPath("data.content[].reviewImageDtos[].seqNo").type(JsonFieldType.NUMBER).description("이미지 시퀀스넘버"),
                                        fieldWithPath("data.content[].reviewImageDtos[].imgUrl").type(JsonFieldType.STRING).description("이미지 URL"),
                                        fieldWithPath("data.content[].reviewImageDtos[].description").type(JsonFieldType.STRING).description("이미지 캡션(설명)"),
                                        fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                        fieldWithPath("data.content[].getReviewSurveyDtos").type(JsonFieldType.ARRAY).description("리뷰서베이DTO리스트"),
                                        fieldWithPath("data.content[].getReviewSurveyDtos[].surveyCategoryName").type(JsonFieldType.STRING).description("서베이카테고리명"),
                                        fieldWithPath("data.content[].getReviewSurveyDtos[].content").type(JsonFieldType.STRING).description("서베이 내용"),
                                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("페이지 정렬 정보"),
                                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("페이지 오프셋"),
                                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 되지 않았는지 여부"),
                                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("페이지 정렬 정보"),
                                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("페이지 정렬 여부"),
                                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("페이지 정렬되었는지 여부"),
                                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("페이지 정렬되지 않았는지 여부"),
                                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬되었는지 여부"),
                                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않았는지 여부"),
                                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("총 요소 수"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
                                )
                                .responseSchema(Schema.schema("GetReviewsMemberMemberIdRes"))
                                .build()))
                );
    }




    @Test
    @DisplayName("상품의 모든 리뷰 조회")
    public void getReviewsOfProduct() throws Exception {
        // given
        Long productId = 1L;

        List<ReviewImageDto> reviewImageDtos = new ArrayList<>();
        ReviewImageDto reviewImageDto1 = ReviewImageDto.builder()
                .seqNo(1L)
                .imgUrl("101.jpg")
                .description("캡션1")
                .build();
        ReviewImageDto reviewImageDto2 = ReviewImageDto.builder()
                .seqNo(2L)
                .imgUrl("102.jpg")
                .description("캡션2")
                .build();
        ReviewImageDto reviewImageDto3 = ReviewImageDto.builder()
                .seqNo(3L)
                .imgUrl("103.jpg")
                .description("캡션3")
                .build();
        reviewImageDtos.add(reviewImageDto1);
        reviewImageDtos.add(reviewImageDto2);
        reviewImageDtos.add(reviewImageDto3);

        List<GetReviewSurveyDto> getReviewSurveyDtos = new ArrayList<>();
        GetReviewSurveyDto reviewSurveyDto1 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리1(질문1)")
                .content("작성한 내용1")
                .build();
        GetReviewSurveyDto reviewSurveyDto2 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리2(질문2)")
                .content("작성한 내용2")
                .build();
        GetReviewSurveyDto reviewSurveyDto3 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리2(질문3)")
                .content("선택한 내용3")
                .build();
        GetReviewSurveyDto reviewSurveyDto4 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리4(질문4)")
                .content("선택한 내용4")
                .build();
        getReviewSurveyDtos.add(reviewSurveyDto1);
        getReviewSurveyDtos.add(reviewSurveyDto2);
        getReviewSurveyDtos.add(reviewSurveyDto3);
        getReviewSurveyDtos.add(reviewSurveyDto4);

        GetReviewDto getReviewDto = GetReviewDto.builder()
                .productThumbnailUrl("thumbnail0001.jpg")
                .memberName("멤버명")
                .rating(5)
                .productName("상품명")
                .summary("한줄요약")
                .reviewImageDtos(reviewImageDtos)
                .content("리뷰내용")
                .getReviewSurveyDtos(getReviewSurveyDtos)
                .build();

        //Dto2
        List<ReviewImageDto> reviewImageDtos2 = new ArrayList<>();
        ReviewImageDto reviewImageDto21 = ReviewImageDto.builder()
                .seqNo(1L)
                .imgUrl("201.jpg")
                .description("캡션1")
                .build();
        ReviewImageDto reviewImageDto22 = ReviewImageDto.builder()
                .seqNo(2L)
                .imgUrl("202.jpg")
                .description("캡션2")
                .build();
        ReviewImageDto reviewImageDto23 = ReviewImageDto.builder()
                .seqNo(3L)
                .imgUrl("203.jpg")
                .description("캡션3")
                .build();
        reviewImageDtos2.add(reviewImageDto21);
        reviewImageDtos2.add(reviewImageDto22);
        reviewImageDtos2.add(reviewImageDto23);

        List<GetReviewSurveyDto> getReviewSurveyDtos2 = new ArrayList<>();
        GetReviewSurveyDto reviewSurveyDto21 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리1(질문1)")
                .content("작성한 내용1")
                .build();
        GetReviewSurveyDto reviewSurveyDto22 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리2(질문2)")
                .content("작성한 내용2")
                .build();
        GetReviewSurveyDto reviewSurveyDto23 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리2(질문3)")
                .content("선택한 내용3")
                .build();
        GetReviewSurveyDto reviewSurveyDto24 = GetReviewSurveyDto.builder()
                .surveyCategoryName("서베이카테고리4(질문4)")
                .content("선택한 내용4")
                .build();
        getReviewSurveyDtos2.add(reviewSurveyDto21);
        getReviewSurveyDtos2.add(reviewSurveyDto22);
        getReviewSurveyDtos2.add(reviewSurveyDto23);
        getReviewSurveyDtos2.add(reviewSurveyDto24);

        GetReviewDto getReviewDto2 = GetReviewDto.builder()
                .productThumbnailUrl("thumbnail0002.jpg")
                .memberName("멤버명")
                .rating(5)
                .productName("상품명")
                .summary("한줄요약")
                .reviewImageDtos(reviewImageDtos2)
                .content("리뷰내용")
                .getReviewSurveyDtos(getReviewSurveyDtos2)
                .build();

        // page 및 sortBy 설정
        // 정렬 선택: createdAt 또는 likes
        int page = 0;
        String sortBy = "createdAt";
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sortBy).descending());

        // 실제 서비스 메서드 호출 결과를 설정
//        Page<GetReviewDto> reviewDtoPage
//                = new PageImpl<>(Collections.singletonList(getReviewDto), pageable, 1);
//        given(reviewService.getReviewsOfProduct(productId, pageable, sortBy)).willReturn(reviewDtoPage);
        //Dto가 2개일때
        List<GetReviewDto> getReviewDtoList = Arrays.asList(getReviewDto, getReviewDto2);
        Page<GetReviewDto> reviewDtoPage
                = new PageImpl<>(getReviewDtoList, pageable, getReviewDtoList.size());
        given(reviewService.getReviewsOfProduct(productId, pageable, sortBy)).willReturn(reviewDtoPage);

        // when // then
        mockMvc.perform(get("/reviews/product/{productId}", productId)
                        .param("productId", String.valueOf(productId))
                        .param("page", String.valueOf(page))
                        .param("sortBy", sortBy)
                        .contentType(MediaType.APPLICATION_JSON))
//                        .header("Authorization", "bearer AccessToken"))
                .andExpect(status().isOk())
                .andDo(document("reviews-of-product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("productId").description("상품 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                        fieldWithPath("data.content[].productThumbnailUrl").type(JsonFieldType.STRING).description("상품 썸네일 이미지"),
                                        fieldWithPath("data.content[].memberName").type(JsonFieldType.STRING).description("멤버 이름"),
                                        fieldWithPath("data.content[].rating").type(JsonFieldType.NUMBER).description("별점"),
                                        fieldWithPath("data.content[].productName").type(JsonFieldType.STRING).description("상품명"),
                                        fieldWithPath("data.content[].summary").type(JsonFieldType.STRING).description("한줄요약"),
                                        fieldWithPath("data.content[].reviewImageDtos").type(JsonFieldType.ARRAY).description("리뷰이미지DTO리스트"),
                                        fieldWithPath("data.content[].reviewImageDtos[].seqNo").type(JsonFieldType.NUMBER).description("이미지 시퀀스넘버"),
                                        fieldWithPath("data.content[].reviewImageDtos[].imgUrl").type(JsonFieldType.STRING).description("이미지 URL"),
                                        fieldWithPath("data.content[].reviewImageDtos[].description").type(JsonFieldType.STRING).description("이미지 캡션(설명)"),
                                        fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                        fieldWithPath("data.content[].getReviewSurveyDtos").type(JsonFieldType.ARRAY).description("리뷰서베이DTO리스트"),
                                        fieldWithPath("data.content[].getReviewSurveyDtos[].surveyCategoryName").type(JsonFieldType.STRING).description("서베이카테고리명"),
                                        fieldWithPath("data.content[].getReviewSurveyDtos[].content").type(JsonFieldType.STRING).description("서베이 내용"),
                                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("페이지 정렬 정보"),
                                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("페이지 오프셋"),
                                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 되지 않았는지 여부"),
                                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("페이지 정렬 정보"),
                                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("페이지 정렬 여부"),
                                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("페이지 정렬되었는지 여부"),
                                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("페이지 정렬되지 않았는지 여부"),
                                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬되었는지 여부"),
                                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않았는지 여부"),
                                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("총 요소 수"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
                                )
                                .responseSchema(Schema.schema("GetReviewsProductProductIdRes"))
                                .build()))
                );
    }
}