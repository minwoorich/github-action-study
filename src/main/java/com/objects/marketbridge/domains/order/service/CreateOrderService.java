package com.objects.marketbridge.domains.order.service;

import com.objects.marketbridge.common.kakao.KakaoPayConfig;
import com.objects.marketbridge.common.kakao.KakaoPayService;
import com.objects.marketbridge.common.kakao.dto.KakaoPayReadyRequest;
import com.objects.marketbridge.common.kakao.dto.KakaoPayReadyResponse;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.service.port.MemberCouponRepository;
import com.objects.marketbridge.domains.member.domain.Address;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.controller.dto.CreateOrderHttp;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.domain.StatusCodeType;
import com.objects.marketbridge.domains.order.service.dto.CreateOrderDto;
import com.objects.marketbridge.domains.order.service.port.AddressRepository;
import com.objects.marketbridge.domains.order.service.port.OrderCommandRepository;
import com.objects.marketbridge.domains.order.service.port.OrderDetailCommandRepository;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.objects.marketbridge.common.kakao.KakaoPayConfig.ONE_TIME_CID;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CreateOrderService {

    private final OrderDetailCommandRepository orderDetailCommandRepository;
    private final OrderCommandRepository orderCommandRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CartCommandRepository cartCommandRepository;
    private final AddressRepository addressRepository;
    private final DateTimeHolder dateTimeHolder;
    private final KakaoPayService kakaoPayService;
    private final KakaoPayConfig kakaoPayConfig;

    @Builder
    public CreateOrderService(OrderDetailCommandRepository orderDetailCommandRepository, OrderCommandRepository orderCommandRepository, ProductRepository productRepository, MemberRepository memberRepository, MemberCouponRepository memberCouponRepository, CartCommandRepository cartCommandRepository, AddressRepository addressRepository, DateTimeHolder dateTimeHolder, KakaoPayService kakaoPayService, KakaoPayConfig kakaoPayConfig) {
        this.orderDetailCommandRepository = orderDetailCommandRepository;
        this.orderCommandRepository = orderCommandRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.memberCouponRepository = memberCouponRepository;
        this.cartCommandRepository = cartCommandRepository;
        this.addressRepository = addressRepository;
        this.dateTimeHolder = dateTimeHolder;
        this.kakaoPayService = kakaoPayService;
        this.kakaoPayConfig = kakaoPayConfig;
    }


    @Transactional
    public void create(CreateOrderDto createOrderDto) {

        // 1. Order 생성
        Order order = orderCommandRepository.save(createOrder(createOrderDto));

        // 2. OrderDetail 생성 (연관관계 매핑 여기서 해결)
        orderDetailCommandRepository.saveAll(createOrderDetails(createOrderDto.getProductValues(), order));

        // 3. MemberCoupon 의 isUsed 변경, 사용날짜 저장
        order.changeMemberCouponInfo(dateTimeHolder);

        // 4. Product 의 stock 감소
        order.stockDecrease();

        // TODO : 장바구니가 아닌 바로구매로 물건을 구매했을 경우 에러 발생 할 수 있음. 해결 해야함.
        // 5. 구매완료한 상품들 장바구니에서 제거하기
        Long memberId = createOrderDto.getMemberId();
        List<Long> deletedProductIds = createOrderDto.getProductValues().stream().map(CreateOrderDto.ProductDto::getProductId).toList();
        cartCommandRepository.deleteAllByProductIdsAndMemberIdInBatch(deletedProductIds, memberId);
    }

    private Order createOrder(CreateOrderDto createOrderDto) {

        Member member = memberRepository.findById(createOrderDto.getMemberId());
        Address address = addressRepository.findById(createOrderDto.getAddressId());
        String orderName = createOrderDto.getOrderName();
        String orderNo = createOrderDto.getOrderNo();
        Long totalOrderPrice = createOrderDto.getTotalOrderPrice();
        Long realOrderPrice = createOrderDto.getRealOrderPrice();
        Long totalDiscountPrice = createOrderDto.getTotalDiscountPrice();
        String tid = createOrderDto.getTid();

        return Order.create(member, address, orderName, orderNo, totalOrderPrice, realOrderPrice, totalDiscountPrice, tid);
    }

    private List<OrderDetail> createOrderDetails(List<CreateOrderDto.ProductDto> productValues, Order order) {

        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CreateOrderDto.ProductDto productValue : productValues) {

            Product product = productRepository.findById(productValue.getProductId());
            // 쿠폰 사용 안 한 경우 그냥 null 저장
            MemberCoupon memberCoupon = productValue.getHasCouponUsed() ? memberCouponRepository.findByMemberIdAndCouponId(order.getMember().getId(), productValue.getCouponId()) : null;
            String orderNo = order.getOrderNo();
            Long quantity = productValue.getQuantity();
            String tid = order.getTid();
            Long price = product.getPrice();

            // OrderDetail 엔티티 생성
            OrderDetail orderDetail =
                    OrderDetail.create(tid, order, product, orderNo, memberCoupon, price, quantity, StatusCodeType.ORDER_INIT.getCode(), dateTimeHolder);

            // orderDetails 에 추가
            orderDetails.add(orderDetail);

            // 연관관계 매핑
            order.addOrderDetail(orderDetail);
        }

        return orderDetails;
    }

    @Transactional
    public KakaoPayReadyResponse ready(CreateOrderHttp.Request createOrderRequest, String orderNo, Long memberId) {
        return kakaoPayService.ready(createKakaoReadyRequest(orderNo, createOrderRequest, memberId));
    }
    private KakaoPayReadyRequest createKakaoReadyRequest(String orderNo, CreateOrderHttp.Request request, Long memberId) {

        String cid = ONE_TIME_CID;
        String approvalUrl = kakaoPayConfig.createApprovalUrl("/payment");
        String failUrl = kakaoPayConfig.getRedirectFailUrl();
        String cancelUrl = kakaoPayConfig.getRedirectCancelUrl();

        return request.toKakaoReadyRequest(orderNo, memberId, cid, approvalUrl, failUrl, cancelUrl);
    }
}
