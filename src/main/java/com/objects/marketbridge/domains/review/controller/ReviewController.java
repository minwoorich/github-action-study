package com.objects.marketbridge.domains.review.controller;

import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.common.security.annotation.AuthMemberId;
import com.objects.marketbridge.common.security.annotation.UserAuthorize;
import com.objects.marketbridge.domains.review.dto.*;
import com.objects.marketbridge.domains.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 작성시 나오는 리뷰서베이 선택창 조회
    //http://localhost:8080/review/surveys?productId=1
    @GetMapping("/review/surveys") //PathVariable -> QueryString으로. (완료)
    @UserAuthorize
    public ApiResponse<List<ReviewSurveyCategoryContentsDto>> getReviewSurveyCategoryContentsList
        (@RequestParam("productId") Long productId){
        List<ReviewSurveyCategoryContentsDto> response
                = reviewService.getReviewSurveyCategoryContentsList(productId);
        return ApiResponse.ok(response);
    }

    //리뷰 등록
    @PostMapping("/review")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewIdDto> createReview
        (@Valid @RequestBody CreateReviewDto request, @AuthMemberId Long memberId) {
        reviewService.createReview(request, memberId);
        return ApiResponse.create();
    }

    //리뷰 수정
    @PatchMapping("/review")
    @UserAuthorize
    public ApiResponse<Void> updateReview(@RequestBody UpdateReviewDto request) {
        reviewService.updateReview(request);
        return ApiResponse.ok(null);
    }

    //리뷰 삭제
    @DeleteMapping("/review/{reviewId}")
    @UserAuthorize
    public ApiResponse<Void> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ApiResponse.ok(null);
    }

    //review_like upsert(없으면 create, 있으면 delete)
    @PostMapping("/review/{reviewId}/like")
    public ApiResponse<Void> upsertReviewLike
    (@PathVariable("reviewId") Long reviewId, @AuthMemberId Long memberId) {
        reviewService.upsertReviewLike(reviewId, memberId);
        return ApiResponse.ok(null);
    }

    //review_like 총갯수 조회
    @GetMapping("/review/{reviewId}/likes/count")
    public ApiResponse<ReviewLikeCountDto> countReviewLike
    (@PathVariable("reviewId") Long reviewId) {
        ReviewLikeCountDto response = reviewService.countReviewLike(reviewId);
        return ApiResponse.ok(response);
    }

    //멤버의 모든 리뷰미작성 주문상세 총갯수 조회
    @GetMapping("/reviews/members/{memberId}/unwritten/count")
    @UserAuthorize
    public ApiResponse<ReviewCountDto> getUnwrittenReviewCountOfMember
    (@AuthMemberId Long memberId){
        ReviewCountDto reviewCountDto = reviewService.getMemberReviewCountUnwritten(memberId);
        return ApiResponse.ok(reviewCountDto);
    }

    //멤버의 모든 리뷰 총갯수 조회
    @GetMapping("/reviews/member/{memberId}/count")
    @UserAuthorize
    public ApiResponse<ReviewCountDto> getMemberReviewsCount
    (@AuthMemberId Long memberId){
        ReviewCountDto reviewCountDto = reviewService.getMemberReviewCount(memberId);
        return ApiResponse.ok(reviewCountDto);
    }

    //상품의 모든 리뷰 총갯수 조회
    @GetMapping("/reviews/product/{productId}/count")
    public ApiResponse<ReviewCountDto> getProductReviewCount
        (@PathVariable("productId") Long productId){
        ReviewCountDto reviewCountDto = reviewService.getProductReviewCount(productId);
        return ApiResponse.ok(reviewCountDto);
    }

    //리뷰아이디로 리뷰 단건 조회
    @GetMapping("/review/{reviewId}")
    @UserAuthorize
    public ApiResponse<GetReviewDto> getReview
    (@PathVariable("reviewId") Long reviewId) {
        GetReviewDto getReviewDto = reviewService.getReview(reviewId);
        return ApiResponse.ok(getReviewDto);
    }

    //멤버의 모든 리뷰미작성 주문상세 조회
    //http://localhost:8080/reviews/unwritten/member/1?page=0
    @GetMapping("/reviews/member/{memberId}/unwritten")
    public ApiResponse<Page<ReviewableDto>> getReviewable(
            @AuthMemberId Long memberId,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageRequest = PageRequest.of(page, 5);
        Page<ReviewableDto> response = reviewService.getReviewable(memberId, pageRequest);
        return ApiResponse.ok(response);
    }

    //멤버의 모든 리뷰 조회(createdAt: 최신순 내림차순 정렬 / likes: 좋아요 많은순 내림차순 정렬
    //http://localhost:8080/reviews/member/1?page=0&sortBy=createdAt
    //http://localhost:8080/reviews/member/1?reviews?page=0&sortBy=likes
    @GetMapping("/reviews/member/{memberId}")
    public ApiResponse<Page<GetReviewDto>> getReviewsOfMember(
            @AuthMemberId Long memberId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy) {
        Pageable pageRequest = PageRequest.of(page, 5, Sort.by(sortBy).descending());
        Page<GetReviewDto> response = reviewService.getReviewsOfMember(memberId, pageRequest, sortBy);
        return ApiResponse.ok(response);
    }

    //상품의 모른 리뷰 조회(createdAt: 최신순 내림차순 정렬 / likes: 좋아요 많은순 내림차순 정렬
    //http://localhost:8080/reviews/product/1?page=0&sortBy=createdAt
    //http://localhost:8080/reviews/product/1?reviews?page=0&sortBy=likes
    @GetMapping("/reviews/product/{productId}")
    public ApiResponse<Page<GetReviewDto>> getReviewsOfProduct(
            @PathVariable("productId") Long productId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy) {
        Pageable pageRequest = PageRequest.of(page, 5, Sort.by(sortBy).descending());
        Page<GetReviewDto> response = reviewService.getReviewsOfProduct(productId, pageRequest, sortBy);
        return ApiResponse.ok(response);
    }
}
