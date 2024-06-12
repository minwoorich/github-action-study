package com.objects.marketbridge.domains.order.controller;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.order.controller.dto.ConfirmCancelHttp;
import com.objects.marketbridge.domains.order.controller.dto.GetCancelDetailHttp;
import com.objects.marketbridge.domains.order.controller.dto.RequestCancelHttp;
import com.objects.marketbridge.domains.order.domain.MemberShipPrice;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.mock.*;
import com.objects.marketbridge.domains.product.domain.Product;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.time.LocalDateTime;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.NON_CANCELLABLE_PRODUCT;
import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.QUANTITY_EXCEEDED;
import static com.objects.marketbridge.domains.order.domain.MemberShipPrice.WOW;
import static com.objects.marketbridge.domains.order.domain.StatusCodeType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

class OrderCancelControllerTest {

    private LocalDateTime orderDate = LocalDateTime.of(2024, 2, 9, 3, 9);
    private LocalDateTime now = LocalDateTime.of(2024, 2, 9, 4, 47);

    private DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
            .now(now)
            .createTime(orderDate)
            .build();

    private TestContainer testContainer = TestContainer.builder()
            .dateTimeHolder(dateTimeHolder)
            .build();

    @AfterEach
    void afterEach() {
        BaseFakeOrderDetailRepository.getInstance().clear();
        BaseFakeOrderRepository.getInstance().clear();
        BaseFakeOrderCancelReturnRepository.getInstance().clear();
        testContainer.memberRepository.deleteAllInBatch();
        testContainer.productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("저장된 주문상세가 없으면 오류가 발생한다.")
    public void confirmCancel_NoOrderDetail_ERROR() {
        // given
        ConfirmCancelHttp.Request request = ConfirmCancelHttp.Request.builder()
                .orderDetailId(1L)
                .build();

        // when // then
        assertThatThrownBy(() -> testContainer.orderCancelController.confirmCancel(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");
    }

    @Test
    @DisplayName("배송완료된 상품이면 에러를 발생시킨다.")
    public void confirmCancel_NO_DELIVERY_COMPLETED_ERROR() {
        // given
        ConfirmCancelHttp.Request request = ConfirmCancelHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfCancellation(2L)
                .reason("단순변심")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .statusCode(DELIVERY_COMPLETED.getCode())
                .build();

        testContainer.orderDetailCommandRepository.save(orderDetail);

        // when // then
        assertThatThrownBy(() -> testContainer.orderCancelController.confirmCancel(request))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage(NON_CANCELLABLE_PRODUCT.getMessage())
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(NON_CANCELLABLE_PRODUCT);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                    assertThat(customLogicException.getTimestamp()).isEqualTo(now);
                });
    }

    @Test
    @DisplayName("취소 수량이 기존 수량보다 많으면 에러가 발생한다.")
    public void confirmCancel_QUANTITY_EXCEEDED_ERROR() {
        // given
        ConfirmCancelHttp.Request request = ConfirmCancelHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfCancellation(2L)
                .reason("단순변심")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .quantity(1L)
                .reducedQuantity(0L)
                .statusCode(ORDER_RECEIVED.getCode())
                .build();

        testContainer.orderDetailCommandRepository.save(orderDetail);

        // when // then
        assertThatThrownBy(() -> testContainer.orderCancelController.confirmCancel(request))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage(QUANTITY_EXCEEDED.getMessage())
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(QUANTITY_EXCEEDED);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                    assertThat(customLogicException.getTimestamp()).isEqualTo(now);
                });
    }

    @Test
    @DisplayName("부분 취소면 줄어든 수량, 상품재고가 변경된다.(NoCoupon)")
    public void confirmCancel_Partial_NoCoupon() {
        // given
        ConfirmCancelHttp.Request request = ConfirmCancelHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfCancellation(2L)
                .reason("단순변심")
                .build();

        Order order = Order.builder()
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .productNo("1")
                .stock(5L)
                .price(1000L)
                .build();

        LocalDateTime cancelledAt = LocalDateTime.of(2024, 2, 11, 3, 23);
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .quantity(3L)
                .orderNo("1")
                .price(1000L)
                .reducedQuantity(0L)
                .statusCode(ORDER_RECEIVED.getCode())
                .cancelledAt(cancelledAt)
                .build();

        testContainer.orderCommandRepository.save(order);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.productRepository.save(product);

        // when
        ApiResponse<ConfirmCancelHttp.Response> result = testContainer.orderCancelController.confirmCancel(request);

        // then
        assertThat(orderDetail.getStatusCode()).isEqualTo(ORDER_CANCEL.getCode());
        assertThat(product.getStock()).isEqualTo(7L);

        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderId()).isEqualTo(1L);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getTotalPrice()).isEqualTo(2000L);
        assertThat(result.getData().getCancellationDate()).isEqualTo(cancelledAt);

        assertThat(result.getData().getCancelledItem().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getCancelledItem().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getCancelledItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getCancelledItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getCancelledItem().getQuantity()).isEqualTo(2L);

        assertThat(result.getData().getRefundInfo().getTotalRefundAmount()).isEqualTo(2000L);
        assertThat(result.getData().getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getData().getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("모두 취소면 상품재고, 줄어든 수량, 상태코드, 이유가 변경된다.(NoCoupon)")
    public void confirmCancel_All_NoCoupon() {
        // given
        ConfirmCancelHttp.Request request = ConfirmCancelHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfCancellation(3L)
                .reason("단순변심")
                .build();

        Order order = Order.builder()
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .productNo("1")
                .stock(5L)
                .price(1000L)
                .build();

        LocalDateTime cancelledAt = LocalDateTime.of(2024, 2, 11, 3, 23);
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .quantity(3L)
                .orderNo("1")
                .price(1000L)
                .reducedQuantity(0L)
                .statusCode(ORDER_RECEIVED.getCode())
                .cancelledAt(cancelledAt)
                .build();

        testContainer.orderCommandRepository.save(order);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.productRepository.save(product);

        // when
        ApiResponse<ConfirmCancelHttp.Response> result = testContainer.orderCancelController.confirmCancel(request);

        // then
        assertThat(orderDetail.getStatusCode()).isEqualTo(ORDER_CANCEL.getCode());
        assertThat(product.getStock()).isEqualTo(8L);

        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderId()).isEqualTo(1L);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getTotalPrice()).isEqualTo(3000L);
        assertThat(result.getData().getCancellationDate()).isEqualTo(cancelledAt);

        assertThat(result.getData().getCancelledItem().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getCancelledItem().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getCancelledItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getCancelledItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getCancelledItem().getQuantity()).isEqualTo(3L);

        assertThat(result.getData().getRefundInfo().getTotalRefundAmount()).isEqualTo(3000L);
        assertThat(result.getData().getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getData().getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("부분 취소면 상품재고, 줄어든 수량이 변경된다.(Coupon)")
    public void confirmCancel_Partial_Coupon() {
        // given
        ConfirmCancelHttp.Request request = ConfirmCancelHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfCancellation(2L)
                .reason("단순변심")
                .build();

        Order order = Order.builder()
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .productNo("1")
                .stock(5L)
                .price(1000L)
                .build();

        Coupon coupon = Coupon.builder()
                .price(1000L)
                .product(product)
                .build();

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .isUsed(true)
                .usedDate(LocalDateTime.now())
                .build();

        LocalDateTime cancelledAt = LocalDateTime.of(2024, 2, 11, 3, 23);
        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(memberCoupon)
                .order(order)
                .product(product)
                .quantity(3L)
                .orderNo("1")
                .price(1000L)
                .reducedQuantity(0L)
                .statusCode(ORDER_RECEIVED.getCode())
                .cancelledAt(cancelledAt)
                .build();

        testContainer.orderCommandRepository.save(order);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.productRepository.save(product);

        // when
        ApiResponse<ConfirmCancelHttp.Response> result = testContainer.orderCancelController.confirmCancel(request);

        // then
        assertThat(memberCoupon.getIsUsed()).isFalse();
        assertThat(memberCoupon.getUsedDate()).isNull();
        assertThat(orderDetail.getStatusCode()).isEqualTo(ORDER_CANCEL.getCode());
        assertThat(product.getStock()).isEqualTo(7L);

        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderId()).isEqualTo(1L);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getTotalPrice()).isEqualTo(2000L);
        assertThat(result.getData().getCancellationDate()).isEqualTo(cancelledAt);

        assertThat(result.getData().getCancelledItem().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getCancelledItem().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getCancelledItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getCancelledItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getCancelledItem().getQuantity()).isEqualTo(2L);

        assertThat(result.getData().getRefundInfo().getTotalRefundAmount()).isEqualTo(2000L);
        assertThat(result.getData().getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getData().getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("모두 반품이면 상품재고, 줄어든 수량, 상태코드, 이유가 변경된다.(Coupon)")
    public void confirmCancel_All_Coupon() {
        // given
        ConfirmCancelHttp.Request request = ConfirmCancelHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfCancellation(3L)
                .reason("단순변심")
                .build();

        Order order = Order.builder()
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .productNo("1")
                .stock(5L)
                .price(1000L)
                .build();

        Coupon coupon = Coupon.builder()
                .price(1000L)
                .product(product)
                .build();

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .isUsed(true)
                .usedDate(LocalDateTime.now())
                .build();

        LocalDateTime cancelledAt = LocalDateTime.of(2024, 2, 11, 3, 23);
        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(memberCoupon)
                .order(order)
                .product(product)
                .quantity(3L)
                .orderNo("1")
                .price(1000L)
                .reducedQuantity(0L)
                .statusCode(ORDER_RECEIVED.getCode())
                .cancelledAt(cancelledAt)
                .build();

        testContainer.orderCommandRepository.save(order);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.productRepository.save(product);

        // when
        ApiResponse<ConfirmCancelHttp.Response> result = testContainer.orderCancelController.confirmCancel(request);

        // then
        assertThat(memberCoupon.getIsUsed()).isFalse();
        assertThat(memberCoupon.getUsedDate()).isNull();
        assertThat(orderDetail.getStatusCode()).isEqualTo(ORDER_CANCEL.getCode());
        assertThat(product.getStock()).isEqualTo(8L);

        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderId()).isEqualTo(1L);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getTotalPrice()).isEqualTo(3000L);
        assertThat(result.getData().getCancellationDate()).isEqualTo(cancelledAt);

        assertThat(result.getData().getCancelledItem().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getCancelledItem().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getCancelledItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getCancelledItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getCancelledItem().getQuantity()).isEqualTo(3L);

        assertThat(result.getData().getRefundInfo().getTotalRefundAmount()).isEqualTo(3000L);
        assertThat(result.getData().getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getData().getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("취소 요청 (WOW_NoCoupon)")
    public void requestCancel_WOW_And_NoCoupon() {
        // given
        Member member = Member.builder()
                .membership(MembershipType.WOW.getText())
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .thumbImg("빵빵이썸네일")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .quantity(10L)
                .product(product)
                .price(1000L)
                .statusCode(PAYMENT_COMPLETED.getCode())
                .reducedQuantity(0L)
                .build();

        testContainer.memberRepository.save(member);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        Long memberId = 1L;

        // when
        ApiResponse<RequestCancelHttp.Response> result = testContainer.orderCancelController.requestCancel(orderDetailId, numberOfCancellation, memberId);

        // then
        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getCancelRefundInfo().getDeliveryFee()).isEqualTo(WOW.getDeliveryFee());
        assertThat(result.getData().getCancelRefundInfo().getRefundFee()).isEqualTo(WOW.getRefundFee());
        assertThat(result.getData().getCancelRefundInfo().getDiscountPrice()).isEqualTo(0L);
        assertThat(result.getData().getCancelRefundInfo().getTotalPrice()).isEqualTo(2000L);

        assertThat(result.getData().getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getProductInfo().getQuantity()).isEqualTo(2L);
        assertThat(result.getData().getProductInfo().getImage()).isEqualTo("빵빵이썸네일");
        assertThat(result.getData().getProductInfo().getPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("취소 요청 (BASIC_NoCoupon)")
    public void requestCancel_BASIC_And_NoCoupon() {
        // given
        Member member = Member.builder()
                .membership(MembershipType.BASIC.getText())
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .thumbImg("빵빵이썸네일")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .quantity(10L)
                .product(product)
                .price(1000L)
                .statusCode(PAYMENT_COMPLETED.getCode())
                .reducedQuantity(0L)
                .build();

        testContainer.memberRepository.save(member);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        Long memberId = 1L;

        // when
        ApiResponse<RequestCancelHttp.Response> result = testContainer.orderCancelController.requestCancel(orderDetailId, numberOfCancellation, memberId);

        // then
        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getCancelRefundInfo().getDeliveryFee()).isEqualTo(MemberShipPrice.BASIC.getDeliveryFee());
        assertThat(result.getData().getCancelRefundInfo().getRefundFee()).isEqualTo(MemberShipPrice.BASIC.getRefundFee());
        assertThat(result.getData().getCancelRefundInfo().getDiscountPrice()).isEqualTo(0L);
        assertThat(result.getData().getCancelRefundInfo().getTotalPrice()).isEqualTo(2000L);

        assertThat(result.getData().getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getProductInfo().getQuantity()).isEqualTo(2L);
        assertThat(result.getData().getProductInfo().getImage()).isEqualTo("빵빵이썸네일");
        assertThat(result.getData().getProductInfo().getPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("취소 요청 (WOW_Coupon)")
    public void requestCancel_WOW_And_Coupon() {
        // given
        Member member = Member.builder()
                .membership(MembershipType.WOW.getText())
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .thumbImg("빵빵이썸네일")
                .build();

        Coupon coupon = Coupon.builder()
                .product(product)
                .price(1000L)
                .name("빵빵이키링 쿠폰")
                .build();

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .member(member)
                .isUsed(true)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(memberCoupon)
                .quantity(10L)
                .product(product)
                .price(1000L)
                .statusCode(PAYMENT_COMPLETED.getCode())
                .reducedQuantity(0L)
                .build();

        testContainer.memberRepository.save(member);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        Long memberId = 1L;

        // when
        ApiResponse<RequestCancelHttp.Response> result = testContainer.orderCancelController.requestCancel(orderDetailId, numberOfCancellation, memberId);

        // then
        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getCancelRefundInfo().getDeliveryFee()).isEqualTo(WOW.getDeliveryFee());
        assertThat(result.getData().getCancelRefundInfo().getRefundFee()).isEqualTo(WOW.getRefundFee());
        assertThat(result.getData().getCancelRefundInfo().getDiscountPrice()).isEqualTo(1000L);
        assertThat(result.getData().getCancelRefundInfo().getTotalPrice()).isEqualTo(2000L);

        assertThat(result.getData().getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getProductInfo().getQuantity()).isEqualTo(2L);
        assertThat(result.getData().getProductInfo().getImage()).isEqualTo("빵빵이썸네일");
        assertThat(result.getData().getProductInfo().getPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("취소 요청 (BASIC_Coupon)")
    public void requestCancel_BASIC_And_Coupon() {
        // given
        Member member = Member.builder()
                .membership(MembershipType.BASIC.getText())
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .thumbImg("빵빵이썸네일")
                .build();

        Coupon coupon = Coupon.builder()
                .product(product)
                .price(1000L)
                .name("빵빵이키링 쿠폰")
                .build();

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .member(member)
                .isUsed(true)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(memberCoupon)
                .quantity(10L)
                .product(product)
                .price(1000L)
                .statusCode(PAYMENT_COMPLETED.getCode())
                .reducedQuantity(0L)
                .build();

        testContainer.memberRepository.save(member);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        Long memberId = 1L;

        // when
        ApiResponse<RequestCancelHttp.Response> result = testContainer.orderCancelController.requestCancel(orderDetailId, numberOfCancellation, memberId);

        // then
        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getCancelRefundInfo().getDeliveryFee()).isEqualTo(MemberShipPrice.BASIC.getDeliveryFee());
        assertThat(result.getData().getCancelRefundInfo().getRefundFee()).isEqualTo(MemberShipPrice.BASIC.getRefundFee());
        assertThat(result.getData().getCancelRefundInfo().getDiscountPrice()).isEqualTo(1000L);
        assertThat(result.getData().getCancelRefundInfo().getTotalPrice()).isEqualTo(2000L);

        assertThat(result.getData().getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getProductInfo().getQuantity()).isEqualTo(2L);
        assertThat(result.getData().getProductInfo().getImage()).isEqualTo("빵빵이썸네일");
        assertThat(result.getData().getProductInfo().getPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("저장된 맴버가 없을 때 취소 요청을 하면 에러가 발생한다.")
    public void requestCancel_NoMember_ERROR() {
        // given
        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        Long memberId = 1L;

        // when // then
        assertThatThrownBy(() -> testContainer.orderCancelController.requestCancel(orderDetailId, numberOfCancellation, memberId))
                .isInstanceOf(JpaObjectRetrievalFailureException.class);
    }

    @Test
    @DisplayName("저장된 주문 상세가 없을 때 취소 요청을 하면 에러가 발생한다.")
    public void requestCancel_ENTITY_NOT_FOUND_ERROR() {

        // given
        Member member = Member.builder()
                .membership(MembershipType.BASIC.getText())
                .build();
        testContainer.memberRepository.save(member);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        Long memberId = 1L;

        // when // then
        assertThatThrownBy(() -> testContainer.orderCancelController.requestCancel(orderDetailId, numberOfCancellation, memberId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");

    }

    @Test
    @DisplayName("주문상세가 취소할 수 없는 상태라면 에러가 발생한다.")
    public void requestCancel_Status_ERROR() {
        // given
        Member member = Member.builder()
                .membership(MembershipType.BASIC.getText())
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .thumbImg("빵빵이썸네일")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .quantity(10L)
                .product(product)
                .price(1000L)
                .statusCode(DELIVERY_COMPLETED.getCode())
                .build();

        testContainer.memberRepository.save(member);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        Long memberId = 1L;

        // when // then
        assertThatThrownBy(() -> testContainer.orderCancelController.requestCancel(orderDetailId, numberOfCancellation, memberId))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("취소가 불가능한 상품입니다.")
                .satisfies(exception -> { CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(NON_CANCELLABLE_PRODUCT);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                });
    }

    @Test
    @DisplayName("취소 상세 조회 (WOW_NoCoupon)")
    public void getCancelDetail_WOW_NoCoupon() {
        // given
        Member member = Member.builder()
                .membership(MembershipType.WOW.getText())
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .productNo("1")
                .build();

        LocalDateTime cancelledAt = LocalDateTime.of(2024, 2, 9, 3, 10);
        OrderDetail orderDetail = OrderDetail.builder()
                .orderNo("1")
                .cancelledAt(cancelledAt)
                .quantity(10L)
                .product(product)
                .reducedQuantity(1L)
                .price(1000L)
                .statusCode(RELEASE_PENDING.getCode())
                .build();

        OrderCancelReturn cancelDetail = OrderCancelReturn.builder()
                .orderDetail(orderDetail)
                .refundAmount(1000L)
                .quantity(1L)
                .statusCode(ORDER_PARTIAL_CANCEL.getCode())
                .reason("단순변심")
                .build();

        testContainer.orderCancelReturnCommandRepository.save(cancelDetail);
        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.memberRepository.save(member);

        Long orderCancelId = 1L;
        Long memberId = 1L;

        // when
        ApiResponse<GetCancelDetailHttp.Response> result = testContainer.orderCancelController.getCancelDetail(orderCancelId, memberId);

        // then
        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderDate()).isEqualTo(orderDate);
        assertThat(result.getData().getCancelDate()).isEqualTo(cancelledAt);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getReason()).isEqualTo("단순변심");

        assertThat(result.getData().getProductInfo().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getProductInfo().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getProductInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getProductInfo().getQuantity()).isEqualTo(10L);

        assertThat(result.getData().getRefundInfo().getDeliveryFee()).isEqualTo(MemberShipPrice.WOW.getDeliveryFee());
        assertThat(result.getData().getRefundInfo().getRefundFee()).isEqualTo(MemberShipPrice.WOW.getRefundFee());
        assertThat(result.getData().getRefundInfo().getDiscountPrice()).isEqualTo(0L);
        assertThat(result.getData().getRefundInfo().getTotalPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("취소 상세 조회 (BASIC_NoCoupon)")
    public void getCancelDetail_BASIC_NoCoupon() {
        // given
        Member member = Member.builder()
                .membership(MembershipType.BASIC.getText())
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .productNo("1")
                .build();

        LocalDateTime cancelledAt = LocalDateTime.of(2024, 2, 9, 3, 10);
        OrderDetail orderDetail = OrderDetail.builder()
                .orderNo("1")
                .cancelledAt(cancelledAt)
                .quantity(10L)
                .reducedQuantity(1L)
                .product(product)
                .price(1000L)
                .statusCode(RELEASE_PENDING.getCode())
                .build();

        OrderCancelReturn cancelDetail = OrderCancelReturn.builder()
                .orderDetail(orderDetail)
                .refundAmount(1000L)
                .quantity(1L)
                .statusCode(ORDER_PARTIAL_CANCEL.getCode())
                .reason("단순변심")
                .build();

        testContainer.orderCancelReturnCommandRepository.save(cancelDetail);
        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.memberRepository.save(member);

        Long orderCancelId = 1L;
        Long memberId = 1L;

        // when
        ApiResponse<GetCancelDetailHttp.Response> result = testContainer.orderCancelController.getCancelDetail(orderCancelId, memberId);

        // then
        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderDate()).isEqualTo(orderDate);
        assertThat(result.getData().getCancelDate()).isEqualTo(cancelledAt);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getReason()).isEqualTo("단순변심");

        assertThat(result.getData().getProductInfo().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getProductInfo().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getProductInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getProductInfo().getQuantity()).isEqualTo(10L);

        assertThat(result.getData().getRefundInfo().getDeliveryFee()).isEqualTo(MemberShipPrice.BASIC.getDeliveryFee());
        assertThat(result.getData().getRefundInfo().getRefundFee()).isEqualTo(MemberShipPrice.BASIC.getRefundFee());
        assertThat(result.getData().getRefundInfo().getDiscountPrice()).isEqualTo(0L);
        assertThat(result.getData().getRefundInfo().getTotalPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("취소 상세 조회 (WOW_Coupon)")
    public void getCancelDetail_WOW_Coupon() {
        // given
        Member member = Member.builder()
                .membership(MembershipType.WOW.getText())
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .productNo("1")
                .build();

        Coupon coupon = Coupon.builder()
                .product(product)
                .price(1000L)
                .build();

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .build();

        LocalDateTime cancelledAt = LocalDateTime.of(2024, 2, 9, 3, 10);
        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(memberCoupon)
                .orderNo("1")
                .cancelledAt(cancelledAt)
                .quantity(10L)
                .product(product)
                .reducedQuantity(1L)
                .price(1000L)
                .statusCode(RELEASE_PENDING.getCode())
                .build();

        OrderCancelReturn cancelDetail = OrderCancelReturn.builder()
                .orderDetail(orderDetail)
                .refundAmount(1000L)
                .quantity(1L)
                .statusCode(ORDER_PARTIAL_CANCEL.getCode())
                .reason("단순변심")
                .build();

        testContainer.orderCancelReturnCommandRepository.save(cancelDetail);
        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.memberRepository.save(member);

        Long orderCancelId = 1L;
        Long memberId = 1L;

        // when
        ApiResponse<GetCancelDetailHttp.Response> result = testContainer.orderCancelController.getCancelDetail(orderCancelId, memberId);

        // then
        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderDate()).isEqualTo(orderDate);
        assertThat(result.getData().getCancelDate()).isEqualTo(cancelledAt);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getReason()).isEqualTo("단순변심");

        assertThat(result.getData().getProductInfo().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getProductInfo().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getProductInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getProductInfo().getQuantity()).isEqualTo(10L);

        assertThat(result.getData().getRefundInfo().getDeliveryFee()).isEqualTo(MemberShipPrice.WOW.getDeliveryFee());
        assertThat(result.getData().getRefundInfo().getRefundFee()).isEqualTo(MemberShipPrice.WOW.getRefundFee());
        assertThat(result.getData().getRefundInfo().getDiscountPrice()).isEqualTo(1000L);
        assertThat(result.getData().getRefundInfo().getTotalPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("취소 상세 조회 (BASIC_Coupon)")
    public void getCancelDetail_BASIC_Coupon() {
        // given
        Member member = Member.builder()
                .membership(MembershipType.BASIC.getText())
                .build();

        Product product = Product.builder()
                .name("빵빵이키링")
                .productNo("1")
                .build();

        Coupon coupon = Coupon.builder()
                .product(product)
                .price(1000L)
                .build();

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .build();

        LocalDateTime cancelledAt = LocalDateTime.of(2024, 2, 9, 3, 10);
        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(memberCoupon)
                .orderNo("1")
                .cancelledAt(cancelledAt)
                .quantity(10L)
                .product(product)
                .reducedQuantity(1L)
                .price(1000L)
                .statusCode(RELEASE_PENDING.getCode())
                .build();

        OrderCancelReturn cancelDetail = OrderCancelReturn.builder()
                .orderDetail(orderDetail)
                .refundAmount(1000L)
                .quantity(1L)
                .statusCode(ORDER_PARTIAL_CANCEL.getCode())
                .reason("단순변심")
                .build();

        testContainer.orderCancelReturnCommandRepository.save(cancelDetail);
        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.memberRepository.save(member);

        Long orderCancelId = 1L;
        Long memberId = 1L;

        // when
        ApiResponse<GetCancelDetailHttp.Response> result = testContainer.orderCancelController.getCancelDetail(orderCancelId, memberId);

        // then
        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderDate()).isEqualTo(orderDate);
        assertThat(result.getData().getCancelDate()).isEqualTo(cancelledAt);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getReason()).isEqualTo("단순변심");

        assertThat(result.getData().getProductInfo().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getProductInfo().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getProductInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getProductInfo().getQuantity()).isEqualTo(10L);

        assertThat(result.getData().getRefundInfo().getDeliveryFee()).isEqualTo(MemberShipPrice.BASIC.getDeliveryFee());
        assertThat(result.getData().getRefundInfo().getRefundFee()).isEqualTo(MemberShipPrice.BASIC.getRefundFee());
        assertThat(result.getData().getRefundInfo().getDiscountPrice()).isEqualTo(1000L);
        assertThat(result.getData().getRefundInfo().getTotalPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("저장된 맴버가 없을 때 취소 요청을 하면 에러가 발생한다.")
    public void getCancelDetail_NoMember_ERROR() {
        // given
        Long orderDetailId = 1L;
        Long memberId = 1L;

        // when // then
        assertThatThrownBy(() -> testContainer.orderCancelController.getCancelDetail(orderDetailId, memberId))
                .isInstanceOf(JpaObjectRetrievalFailureException.class);
    }

    @Test
    @DisplayName("주문 상세가 없을 경우 에러가 발생한다.")
    public void getCancelDetail_ENTITY_NOT_FOUND_ERROR() {
        // given
        Member member = Member.builder().build();

        testContainer.memberRepository.save(member);

        Long orderDetailId = 1L;
        Long memberId = 1L;

        // when
        assertThatThrownBy(() -> testContainer.orderCancelController.getCancelDetail(orderDetailId, memberId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");
    }

}