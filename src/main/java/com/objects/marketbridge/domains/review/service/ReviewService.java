package com.objects.marketbridge.domains.review.service;

import com.objects.marketbridge.domains.image.domain.Image;
import com.objects.marketbridge.domains.image.infra.ImageRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.domain.StatusCodeType;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.review.domain.*;
import com.objects.marketbridge.domains.review.dto.*;
import com.objects.marketbridge.domains.review.service.port.*;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
//@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final SurveyCategoryRepository surveyCategoryRepository;
    private final SurveyContentRepository surveyContentRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final OrderDetailReviewRepository orderDetailReviewRepository;

    @Builder
    public ReviewService(ReviewRepository reviewRepository, ImageRepository imageRepository,
                         ReviewImageRepository reviewImageRepository, SurveyCategoryRepository surveyCategoryRepository,
                         SurveyContentRepository surveyContentRepository, ReviewLikeRepository reviewLikeRepository,
                         OrderDetailReviewRepository orderDetailReviewRepository) {
        this.reviewRepository = reviewRepository;
        this.imageRepository = imageRepository;
        this.reviewImageRepository = reviewImageRepository;
        this.surveyCategoryRepository = surveyCategoryRepository;
        this.surveyContentRepository = surveyContentRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.orderDetailReviewRepository = orderDetailReviewRepository;
    }

    //리뷰 작성시 나오는 리뷰서베이 선택창 조회

    //1차(리팩토링전)
//    @Transactional
//    public List<ReviewSurveyCategoryContentsDto> getReviewSurveyCategoryContentsList (Long productId) {
//        //해당 상품에 대한 SurveyCategory들을 가져옴(선택창+입력창인 경우 모두)
//        //해당 상품에 대한 SurveyContents들을 가져옴(선택창일 경우만)
//        //해당 상품에 대한 입력창에 입력할 SurveyContent는 프론트에서 처리해서 리뷰등록을 해야되게.
//
//        //서베이카테고리들
//        List<SurveyCategory> surveyCategories
//                = surveyCategoryRepository.findAllByProductId(productId);
//
//        ReviewSurveyCategoryContentsDto reviewSurveyCategoryContentsDto;
//
//        List<ReviewSurveyCategoryContentsDto> reviewSurveyCategoryContentsDtos = new ArrayList<>();
//
//        for (SurveyCategory surveyCategory : surveyCategories) {
//
//            if (surveyContentRepository.existsBySurveyCategoryId(surveyCategory.getId())) {
//                List<SurveyContent> surveyContents
//                        = surveyContentRepository.findAllBySurveyCategoryId(surveyCategory.getId());
//                List<String> surveyContentsForDto = new ArrayList<>();
//
//                for (SurveyContent surveyContent : surveyContents) {
//                    surveyContentsForDto.add(surveyContent.getContent());
//                }
//                reviewSurveyCategoryContentsDto
//                        = ReviewSurveyCategoryContentsDto.builder()
//                        .category(surveyCategory.getName())
//                        .contents(surveyContentsForDto)
//                        .build();
//                reviewSurveyCategoryContentsDtos.add(reviewSurveyCategoryContentsDto);
//
//            } else {
//                reviewSurveyCategoryContentsDto
//                        = ReviewSurveyCategoryContentsDto.builder()
//                        .category(surveyCategory.getName())
//                        .contents(null)
//                        .build();
//                reviewSurveyCategoryContentsDtos.add(reviewSurveyCategoryContentsDto);
//            }
//        }
//        return reviewSurveyCategoryContentsDtos;
//    }

    //2차 리팩토링
//    @Transactional
//    public List<ReviewSurveyCategoryContentsDto> getReviewSurveyCategoryContentsList (Long productId) {
//        //해당 상품에 대한 SurveyCategory들을 가져옴(선택창+입력창인 경우 모두)
//        //해당 상품에 대한 SurveyContents들을 가져옴(선택창일 경우만)
//        //해당 상품에 대한 입력창에 입력할 SurveyContent는 프론트에서 처리해서 리뷰등록을 해야되게.
//
//        //서베이카테고리들
//        List<SurveyCategory> surveyCategories
//                = surveyCategoryRepository.findAllByProductId(productId);
//
//        //response용 dtos
//        List<ReviewSurveyCategoryContentsDto> reviewSurveyCategoryContentsDtos
//                = surveyCategories.stream()
//                .map(surveyCategory -> {
//                    List<String> contentsValues = null;
//
//                    if (surveyContentRepository.existsBySurveyCategoryId(surveyCategory.getId())) {
//                        contentsValues = new ArrayList<>();
//
//                        List<SurveyContent> surveyContents
//                                = surveyContentRepository.findAllBySurveyCategoryId(surveyCategory.getId());
//                        for (SurveyContent surveyContent : surveyContents) {
//                            contentsValues.add(surveyContent.getContent());
//                        }
//                    }
//
//                    return ReviewSurveyCategoryContentsDto.builder()
//                            .category(surveyCategory.getName())
//                            .contents(contentsValues)
//                            .build();
//                }).collect(Collectors.toList());
//
//        return reviewSurveyCategoryContentsDtos;
//    }

    //3차 리팩토링
//    @Transactional
//    public List<ReviewSurveyCategoryContentsDto> getReviewSurveyCategoryContentsList (Long productId) {
//        //해당 상품에 대한 SurveyCategory들을 가져옴(선택창+입력창인 경우 모두)
//        //해당 상품에 대한 SurveyContents들을 가져옴(선택창일 경우만)
//        //해당 상품에 대한 입력창에 입력할 SurveyContent는 프론트에서 처리해서 리뷰등록을 해야되게.
//
//        //서베이카테고리들
//        List<SurveyCategory> surveyCategories
//                = surveyCategoryRepository.findAllByProductId(productId);
//
//        //response용 dtos
//        List<ReviewSurveyCategoryContentsDto> reviewSurveyCategoryContentsDtos
//                = surveyCategories.stream()
//                .map(surveyCategory -> {
//                    List<String> contentsValues = null;
//
//                    if (surveyContentRepository.existsBySurveyCategoryId(surveyCategory.getId())) {
//                        List<SurveyContent> surveyContents
//                                = surveyContentRepository.findAllBySurveyCategoryId(surveyCategory.getId());
//
//                        contentsValues = surveyContents.stream()
//                                .map(SurveyContent::getContent)
//                                .collect(Collectors.toList());
//                    }
//
//                    return ReviewSurveyCategoryContentsDto.builder()
//                            .category(surveyCategory.getName())
//                            .contents(contentsValues)
//                            .build();
//                }).collect(Collectors.toList());
//
//        return reviewSurveyCategoryContentsDtos;
//    }

    //4차 리팩토링
    @Transactional
    public List<ReviewSurveyCategoryContentsDto> getReviewSurveyCategoryContentsList(Long productId) {
        //해당 상품에 대한 SurveyCategory들을 가져옴(선택창+입력창인 경우 모두)
        List<SurveyCategory> surveyCategories = surveyCategoryRepository.findAllByProductId(productId);

        // SurveyCategoryIds 추출
        List<Long> surveyCategoryIds = surveyCategories.stream()
                .map(SurveyCategory::getId)
                .collect(Collectors.toList());

        // 해당 SurveyCategoryIds에 해당하는 SurveyContents를 한 번에 가져옴
        List<SurveyContent> surveyContents = surveyContentRepository.findAllBySurveyCategoryIdIn(surveyCategoryIds);

        // Map을 사용하여 SurveyCategory와 SurveyContent를 그룹화
        Map<Long, List<SurveyContent>> surveyContentMap
                = surveyContents.stream()
                .collect(Collectors.groupingBy(SurveyContent::getSurveyCategoryId));

        // response용 dtos 생성
        List<ReviewSurveyCategoryContentsDto> reviewSurveyCategoryContentsDtos = surveyCategories.stream()
                .map(surveyCategory -> {
                    List<String> contentsValues = null;

                    // SurveyCategory에 해당하는 SurveyContents가 존재하면 contentsValues 설정
                    if (surveyContentMap.containsKey(surveyCategory.getId())) {
                        List<SurveyContent> contents = surveyContentMap.get(surveyCategory.getId());
                        contentsValues = contents.stream()
                                .map(SurveyContent::getContent)
                                .collect(Collectors.toList());
                    }

                    return ReviewSurveyCategoryContentsDto.builder()
                            .category(surveyCategory.getName())
                            .contents(contentsValues)
                            .build();
                }).collect(Collectors.toList());

        return reviewSurveyCategoryContentsDtos;
    }




    //리뷰 등록
    @Transactional
    public void createReview(CreateReviewDto request, Long memberId) {

        //로그인한 유저여야 하므로, memberId는 파라미터로 받아 사용.
        Integer rating = request.getRating();
        String content = request.getContent();
        String summary = request.getSummary();

        Member member = Member.builder().id(memberId).build();
        Product product = Product.builder().id(request.getProductId()).build();

        //이미지 저장 및 리뷰이미지 저장
        Review review = Review.builder()
                .member(member)
                .product(product)
                .rating(rating)
                .content(content)
                .summary(summary)
                .build();

        createReviewImages(request.getReviewImages(), review);

        //선택&입력된 리뷰서베이들 등록
        createReviewSurveys(request, review);

        reviewRepository.save(review);
    }




    //리뷰 수정
    @Transactional
    public void updateReview(UpdateReviewDto request) {

        Long reviewId = request.getReviewId();

        Review review = reviewRepository.findById(reviewId);

        deleteReviewImages(review);

        updateReviewSurveys(request, review);
        createReviewImages(request.getReviewImages(), review);

        review.update(request.getRating(), request.getContent(), request.getSummary());
    }




    //리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId){
//        List<ReviewLike> reviewLikes = reviewLikeRepository.findAllByReviewId(reviewId);
//        List<Long> reviewLikeIds
//                = reviewLikes.stream().map(ReviewLike::getId).collect(Collectors.toList());
//        reviewLikeRepository.deleteAllByIdIn(reviewLikeIds);
        reviewLikeRepository.deleteAllByReviewId(reviewId);
        reviewRepository.deleteById(reviewId);
    }




    //review_like upsert(없으면 create, 있으면 delete)
    @Transactional
    public void upsertReviewLike(Long reviewId, Long memberId){

        Review review = Review.builder().id(reviewId).build();
        Member member = Member.builder().id(memberId).build();

        if(reviewLikeRepository.existsByReviewIdAndMemberId(reviewId, memberId)){
            reviewLikeRepository.deleteByReviewIdAndMemberId(reviewId, memberId);
        } else {
            ReviewLike reviewLike = ReviewLike.builder()
                    .review(review)
                    .member(member)
                    .build();
            reviewLikeRepository.save(reviewLike);
        }
    }




    //review_like 총갯수 조회
    @Transactional
    public ReviewLikeCountDto countReviewLike(Long reviewId) {
        Long count = reviewLikeRepository.countByReviewId(reviewId);
        ReviewLikeCountDto reviewLikeCountDto
                = ReviewLikeCountDto.builder().count(count).build();
        return reviewLikeCountDto;
    }




    //멤버의 모든 리뷰미작성 주문상세 총갯수 조회
    @Transactional
    public ReviewCountDto getMemberReviewCountUnwritten(Long memberId) {
        Long countAll
                = orderDetailReviewRepository
                .countByMemberIdAndStatusCode(memberId, StatusCodeType.DELIVERY_COMPLETED.getCode());
        Long countWritten = reviewRepository.countByMemberId(memberId);
        Long countUnwritten = countAll - countWritten;
        return ReviewCountDto.builder().count(countUnwritten).build();
    }




    //멤버의 모든 리뷰 총갯수 조회
    @Transactional
    public ReviewCountDto getMemberReviewCount(Long memberId) {
        Long count = reviewRepository.countByMemberId(memberId);
        return ReviewCountDto.builder().count(count).build();
    }




    //상품의 모든 리뷰 총갯수 조회
    @Transactional
    public ReviewCountDto getProductReviewCount(Long productId) {
        Long count = reviewRepository.countByProductId(productId);
        return ReviewCountDto.builder().count(count).build();
    }





    //리뷰아이디로 리뷰 단건 조회
    @Transactional
    public GetReviewDto getReview(Long reviewId){
//        Review review = Review.builder().id(reviewId).build(); //멤버가 없어서 NullPointerException남.
        Review review = reviewRepository.findById(reviewId);

        List<ReviewImage> reviewImages = review.getReviewImages();
        List<ReviewImageDto> reviewImageDtos
                = reviewImages.stream()
                .map(reviewImage -> ReviewImageDto.builder()
                        .seqNo(reviewImage.getSeqNo())
                        .imgUrl(reviewImage.getImage().getUrl())
                        .description(reviewImage.getDescription())
                        .build())
                .collect(Collectors.toList());

        List<ReviewSurvey> reviewSurveys = review.getReviewSurveys();
        List<GetReviewSurveyDto> getReviewSurveyDtos
                = reviewSurveys.stream()
                .map(reviewSurvey -> GetReviewSurveyDto.builder()
                        .surveyCategoryName(reviewSurvey.getSurveyCategory())
                        .content(reviewSurvey.getContent())
                        .build())
                .collect(Collectors.toList());

        GetReviewDto getReviewDto
                = GetReviewDto.builder()
                .productThumbnailUrl(review.getProduct().getThumbImg())
                .memberName(review.getMember().getName())
                .productName(review.getProduct().getName())
                .summary(review.getSummary())
                .rating(review.getRating())
                .reviewImageDtos(reviewImageDtos)
                .content(review.getContent())
                .getReviewSurveyDtos(getReviewSurveyDtos)
                .build();
        return getReviewDto;
    }




    //멤버의 모든 리뷰미작성 주문상세들 조회
    public Page<ReviewableDto> getReviewable(Long memberId, Pageable pageable) {

        Page<OrderDetail> orderDetails = orderDetailReviewRepository.findAllByMemberIdAndStatusCode
                (memberId, StatusCodeType.DELIVERY_COMPLETED.getCode(), pageable);
        List<ReviewableDto> reviewableDtos
                = orderDetails.stream()
                .map(orderDetail ->
                        ReviewableDto.builder()
                                .productThumbnailUrl(orderDetail.getProduct().getThumbImg())
                                .productName(orderDetail.getProduct().getName())
                                .deliveredDate(orderDetail.getDeliveredDate())
                                .build())
                .collect(Collectors.toList());
        return new PageImpl<>(reviewableDtos, pageable, orderDetails.getTotalElements());
    }




    //멤버의 모든 리뷰 조회(createdAt: 최신순 내림차순 정렬 / likes: 좋아요많은순 내림차순 정렬)
    @Transactional
    public Page<GetReviewDto> getReviewsOfMember(Long memberId, Pageable pageable, String sortBy) {
        Page<Review> reviews;
        if (sortBy.equals("likes")) {
            reviews = reviewRepository.findAllByMemberIdOrderByLikesDesc(memberId, pageable);
        } else {
            reviews = reviewRepository.findAllByMemberId(memberId, pageable);
        }

        List<GetReviewDto> getReviewDtos = reviews.stream()
                .map(review -> getReview(review.getId()))
                .collect(Collectors.toList());
        return new PageImpl<>(getReviewDtos, pageable, reviews.getTotalElements());
    }




    //상품의 모든 리뷰 조회(createdAt: 최신순 내림차순 정렬 / likes: 좋아요많은순 내림차순 정렬)
    @Transactional
    public Page<GetReviewDto> getReviewsOfProduct(Long productId, Pageable pageable, String sortBy) {
        Page<Review> reviews;
        if (sortBy.equals("likes")) {
            reviews = reviewRepository.findAllByProductIdOrderByLikesDesc(productId, pageable);
        } else {
            reviews = reviewRepository.findAllByProductId(productId, pageable);
        }

        List<GetReviewDto> getReviewDtos = reviews.stream()
                .map(review -> getReview(review.getId()))
                .collect(Collectors.toList());
        return new PageImpl<>(getReviewDtos, pageable, reviews.getTotalElements());
    }




    //=====내부 이용 메서드=====
    private void createReviewSurveys(CreateReviewDto request, Review review) {
        request.getReviewSurveys().forEach(obj -> {
            ReviewSurvey reviewSurvey = ReviewSurvey.builder()
                    .review(review)
                    .surveyCategoryId(obj.getSurveyCategoryId())
                    .surveyCategory(obj.getSurveyCategoryName())
                    .content(obj.getContent())
                    .build();

            review.addReviewSurveys(reviewSurvey);
        });
    }

    private void createReviewImages(List<ReviewImageDto> request, Review review) {
        request.forEach(obj -> {
            Image image = Image.builder()
                    .url(obj.getImgUrl())
                    .build();
            imageRepository.save(image);

            ReviewImage reviewImage = ReviewImage.builder()
                    .review(review)
                    .image(image)
                    .seqNo(obj.getSeqNo())
                    .description(obj.getDescription())
                    .build();
            image.setReviewImage(reviewImage);
            review.addReviewImages(reviewImage);
        });
    }

    private void updateReviewSurveys(UpdateReviewDto request, Review review) {
        review.getReviewSurveys().forEach(reviewSurvey -> {
            UpdateReviewSurveyDto updateReviewSurveyDto =
                    request.getUpdateReviewSurveys().stream()
                            .filter(obj -> Objects.equals(obj.getReviewSurveyId(), reviewSurvey.getId()))
                            .findFirst()
                            .orElse(new UpdateReviewSurveyDto(reviewSurvey.getId(), reviewSurvey.getContent()));
            reviewSurvey.update(updateReviewSurveyDto.getContent());
        });

    }

    private void deleteReviewImages(Review review) {
        List<ReviewImage> reviewImages = review.getReviewImages();
        reviewImageRepository.deleteAllByIdInBatch(reviewImages.stream().map(ReviewImage::getId).toList());
        imageRepository.deleteAllByIdInBatch(reviewImages.stream().map(obj -> obj.getImage().getId()).toList());
    }
}
