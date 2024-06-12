package com.objects.marketbridge.domains.order.controller;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.order.controller.dto.ConfirmReturnHttp;
import com.objects.marketbridge.domains.order.controller.dto.GetReturnDetailHttp;
import com.objects.marketbridge.domains.order.controller.dto.RequestReturnHttp;
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

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.NON_RETURNABLE_PRODUCT;
import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.QUANTITY_EXCEEDED;
import static com.objects.marketbridge.domains.order.domain.MemberShipPrice.WOW;
import static com.objects.marketbridge.domains.order.domain.StatusCodeType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

class OrderReturnControllerTest {

    private LocalDateTime orderDate = LocalDateTime.of(2024, 2, 9, 3, 9);
    private LocalDateTime now = LocalDateTime.of(2024, 2, 11, 4, 28);
    private TestContainer testContainer = TestContainer.builder()
            .dateTimeHolder(
                    TestDateTimeHolder.builder()
                            .createTime(orderDate)
                            .now(now)
                            .build()
            )
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
    public void confirmReturn_NoOrderDetail_ERROR() {
        // given
        ConfirmReturnHttp.Request request = ConfirmReturnHttp.Request.builder()
                .orderDetailId(1L)
                .build();

        // when // then
        assertThatThrownBy(() -> testContainer.orderReturnController.confirmReturn(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");
    }

    @Test
    @DisplayName("배송완료된 상품이 아니면 에러를 발생시킨다.")
    public void confirmReturn_NO_DELIVERY_COMPLETED_ERROR() {
        // given
        ConfirmReturnHttp.Request request = ConfirmReturnHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfReturns(2L)
                .reason("단순변심")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .statusCode(PAYMENT_PENDING.getCode())
                .build();

        testContainer.orderDetailCommandRepository.save(orderDetail);

        // when // then
        assertThatThrownBy(() -> testContainer.orderReturnController.confirmReturn(request))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage(NON_RETURNABLE_PRODUCT.getMessage())
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(NON_RETURNABLE_PRODUCT);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                    assertThat(customLogicException.getTimestamp()).isEqualTo(now);
                });
    }

    @Test
    @DisplayName("반품 수량이 기존 수량보다 많으면 에러가 발생한다.")
    public void confirmReturn_QUANTITY_EXCEEDED_ERROR() {
        // given
        ConfirmReturnHttp.Request request = ConfirmReturnHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfReturns(2L)
                .reason("단순변심")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .quantity(1L)
                .reducedQuantity(0L)
                .statusCode(DELIVERY_COMPLETED.getCode())
                .build();

        testContainer.orderDetailCommandRepository.save(orderDetail);

        // when // then
        assertThatThrownBy(() -> testContainer.orderReturnController.confirmReturn(request))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("수량이 초과 되었습니다.")
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(QUANTITY_EXCEEDED);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                    assertThat(customLogicException.getTimestamp()).isEqualTo(now);
                });
    }

    @Test
    @DisplayName("부분 반품이면 줄어든 수량, 상품재고가 변경된다.(NoCoupon)")
    public void confirmReturn_Partial_NoCoupon() {
        // given
        ConfirmReturnHttp.Request request = ConfirmReturnHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfReturns(2L)
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
                .statusCode(DELIVERY_COMPLETED.getCode())
                .cancelledAt(cancelledAt)
                .build();

        testContainer.orderCommandRepository.save(order);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.productRepository.save(product);

        // when
        ApiResponse<ConfirmReturnHttp.Response> result = testContainer.orderReturnController.confirmReturn(request);

        // then
        assertThat(orderDetail.getStatusCode()).isEqualTo(RETURN_INIT.getCode());
        assertThat(product.getStock()).isEqualTo(7L);

        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderId()).isEqualTo(1L);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getTotalPrice()).isEqualTo(2000L);
        assertThat(result.getData().getReturnedDate()).isEqualTo(cancelledAt);

        assertThat(result.getData().getReturnedItem().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getReturnedItem().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getReturnedItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getReturnedItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getReturnedItem().getQuantity()).isEqualTo(2L);

        assertThat(result.getData().getRefundInfo().getTotalRefundAmount()).isEqualTo(2000L);
        assertThat(result.getData().getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getData().getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("모두 반품이면 상품재고, 줄어든 수량, 상태코드, 이유가 변경된다.(NoCoupon)")
    public void confirmReturn_All_NoCoupon() {
        // given
        ConfirmReturnHttp.Request request = ConfirmReturnHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfReturns(3L)
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
                .statusCode(DELIVERY_COMPLETED.getCode())
                .cancelledAt(cancelledAt)
                .build();

        testContainer.orderCommandRepository.save(order);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.productRepository.save(product);

        // when
        ApiResponse<ConfirmReturnHttp.Response> result = testContainer.orderReturnController.confirmReturn(request);

        // then
        assertThat(orderDetail.getStatusCode()).isEqualTo(RETURN_INIT.getCode());
        assertThat(product.getStock()).isEqualTo(8L);

        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderId()).isEqualTo(1L);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getTotalPrice()).isEqualTo(3000L);
        assertThat(result.getData().getReturnedDate()).isEqualTo(cancelledAt);

        assertThat(result.getData().getReturnedItem().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getReturnedItem().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getReturnedItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getReturnedItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getReturnedItem().getQuantity()).isEqualTo(3L);

        assertThat(result.getData().getRefundInfo().getTotalRefundAmount()).isEqualTo(3000L);
        assertThat(result.getData().getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getData().getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("부분 반품이면 상품재고, 줄어든 수량이 변경된다.(Coupon)")
    public void confirmReturn_Partial_Coupon() {
        // given
        ConfirmReturnHttp.Request request = ConfirmReturnHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfReturns(2L)
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
                .statusCode(DELIVERY_COMPLETED.getCode())
                .cancelledAt(cancelledAt)
                .build();

        testContainer.orderCommandRepository.save(order);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.productRepository.save(product);

        // when
        ApiResponse<ConfirmReturnHttp.Response> result = testContainer.orderReturnController.confirmReturn(request);

        // then
        assertThat(memberCoupon.getIsUsed()).isFalse();
        assertThat(memberCoupon.getUsedDate()).isNull();
        assertThat(orderDetail.getStatusCode()).isEqualTo(RETURN_INIT.getCode());
        assertThat(product.getStock()).isEqualTo(7L);

        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderId()).isEqualTo(1L);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getTotalPrice()).isEqualTo(2000L);
        assertThat(result.getData().getReturnedDate()).isEqualTo(cancelledAt);

        assertThat(result.getData().getReturnedItem().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getReturnedItem().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getReturnedItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getReturnedItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getReturnedItem().getQuantity()).isEqualTo(2L);

        assertThat(result.getData().getRefundInfo().getTotalRefundAmount()).isEqualTo(2000L);
        assertThat(result.getData().getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getData().getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("모두 반품이면 상품재고, 줄어든 수량, 상태코드, 이유가 변경된다.(Coupon)")
    public void confirmReturn_All_Coupon() {
        // given
        ConfirmReturnHttp.Request request = ConfirmReturnHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfReturns(3L)
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
                .statusCode(DELIVERY_COMPLETED.getCode())
                .cancelledAt(cancelledAt)
                .build();

        testContainer.orderCommandRepository.save(order);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.productRepository.save(product);

        // when
        ApiResponse<ConfirmReturnHttp.Response> result = testContainer.orderReturnController.confirmReturn(request);

        // then
        assertThat(memberCoupon.getIsUsed()).isFalse();
        assertThat(memberCoupon.getUsedDate()).isNull();
        assertThat(orderDetail.getStatusCode()).isEqualTo(RETURN_INIT.getCode());
        assertThat(product.getStock()).isEqualTo(8L);

        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getOrderId()).isEqualTo(1L);
        assertThat(result.getData().getOrderNo()).isEqualTo("1");
        assertThat(result.getData().getTotalPrice()).isEqualTo(3000L);
        assertThat(result.getData().getReturnedDate()).isEqualTo(cancelledAt);

        assertThat(result.getData().getReturnedItem().getProductId()).isEqualTo(1L);
        assertThat(result.getData().getReturnedItem().getProductNo()).isEqualTo("1");
        assertThat(result.getData().getReturnedItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getReturnedItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getData().getReturnedItem().getQuantity()).isEqualTo(3L);

        assertThat(result.getData().getRefundInfo().getTotalRefundAmount()).isEqualTo(3000L);
        assertThat(result.getData().getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getData().getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("반품 요청 (WOW)")
    public void requestReturn_WOW() {
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
                .reducedQuantity(0L)
                .statusCode(DELIVERY_COMPLETED.getCode())
                .build();

        testContainer.memberRepository.save(member);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfReturns = 2L;
        Long memberId = 1L;

        // when
        ApiResponse<RequestReturnHttp.Response> result = testContainer.orderReturnController.requestReturn(orderDetailId, numberOfReturns, memberId);

        // then
        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getReturnRefundInfo().getDeliveryFee()).isEqualTo(WOW.getDeliveryFee());
        assertThat(result.getData().getReturnRefundInfo().getReturnFee()).isEqualTo(WOW.getReturnFee());
        assertThat(result.getData().getReturnRefundInfo().getProductTotalPrice()).isEqualTo(2000L);

        assertThat(result.getData().getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getProductInfo().getQuantity()).isEqualTo(2L);
        assertThat(result.getData().getProductInfo().getImage()).isEqualTo("빵빵이썸네일");
        assertThat(result.getData().getProductInfo().getPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("반품 요청 (BASIC)")
    public void requestReturn_BASIC() {
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
                .reducedQuantity(0L)
                .statusCode(DELIVERY_COMPLETED.getCode())
                .build();

        testContainer.memberRepository.save(member);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfReturns = 2L;
        Long memberId = 1L;

        // when
        ApiResponse<RequestReturnHttp.Response> result = testContainer.orderReturnController.requestReturn(orderDetailId, numberOfReturns, memberId);

        // then
        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());

        assertThat(result.getData().getReturnRefundInfo().getDeliveryFee()).isEqualTo(MemberShipPrice.BASIC.getDeliveryFee());
        assertThat(result.getData().getReturnRefundInfo().getReturnFee()).isEqualTo(MemberShipPrice.BASIC.getReturnFee());
        assertThat(result.getData().getReturnRefundInfo().getProductTotalPrice()).isEqualTo(2000L);

        assertThat(result.getData().getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getData().getProductInfo().getQuantity()).isEqualTo(2L);
        assertThat(result.getData().getProductInfo().getImage()).isEqualTo("빵빵이썸네일");
        assertThat(result.getData().getProductInfo().getPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("저장된 맴버가 없을 때 취소 요청을 하면 에러가 발생한다.")
    public void requestReturn_NoMember_ERROR() {
        // given
        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        Long memberId = 1L;

        // when // then
        assertThatThrownBy(() -> testContainer.orderReturnController.requestReturn(orderDetailId, numberOfCancellation, memberId))
                .isInstanceOf(JpaObjectRetrievalFailureException.class);
    }

    @Test
    @DisplayName("저장된 주문 상세가 없을 때 취소 요청을 하면 에러가 발생한다.")
    public void requestReturn_ENTITY_NOT_FOUND_ERROR() {
        // given
        Member member = Member.builder()
                .membership(MembershipType.BASIC.getText())
                .build();
        testContainer.memberRepository.save(member);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        Long memberId = 1L;

        // when // then
        assertThatThrownBy(() -> testContainer.orderReturnController.requestReturn(orderDetailId, numberOfCancellation, memberId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");
    }

    @Test
    @DisplayName("주문상세가 반품할 수 없는 상태라면 에러가 발생한다.")
    public void requestReturn_Status_ERROR() {
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
                .statusCode(RETURN_COMPLETED.getCode())
                .build();

        testContainer.memberRepository.save(member);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        Long memberId = 1L;

        // when // then
        assertThatThrownBy(() -> testContainer.orderReturnController.requestReturn(orderDetailId, numberOfCancellation, memberId))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("반품이 불가능한 상품입니다.")
                .satisfies(exception -> { CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(NON_RETURNABLE_PRODUCT);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                });
    }

    @Test
    @DisplayName("반품 상세 조회 (WOW_NoCoupon)")
    public void getReturnDetail_WOW_NoCoupon() {
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

        OrderCancelReturn returnDetail = OrderCancelReturn.builder()
                .orderDetail(orderDetail)
                .refundAmount(1000L)
                .quantity(1L)
                .statusCode(ORDER_PARTIAL_CANCEL.getCode())
                .reason("단순변심")
                .build();

        testContainer.orderCancelReturnCommandRepository.save(returnDetail);
        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.memberRepository.save(member);

        Long orderReturnId = 1L;
        Long memberId = 1L;

        // when
        ApiResponse<GetReturnDetailHttp.Response> result = testContainer.orderReturnController.getReturnDetail(orderReturnId, memberId);

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
    @DisplayName("반품 상세 조회 (BASIC_NoCoupon)")
    public void getReturnDetail_BASIC_NoCoupon() {
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

        OrderCancelReturn returnDetail = OrderCancelReturn.builder()
                .orderDetail(orderDetail)
                .refundAmount(1000L)
                .quantity(1L)
                .statusCode(ORDER_PARTIAL_CANCEL.getCode())
                .reason("단순변심")
                .build();

        testContainer.orderCancelReturnCommandRepository.save(returnDetail);
        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.memberRepository.save(member);

        Long orderReturnId = 1L;
        Long memberId = 1L;

        // when
        ApiResponse<GetReturnDetailHttp.Response> result = testContainer.orderReturnController.getReturnDetail(orderReturnId, memberId);

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
    @DisplayName("반품 상세 조회 (WOW_Coupon)")
    public void getReturnDetail_WOW_Coupon() {
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

        OrderCancelReturn returnDetail = OrderCancelReturn.builder()
                .orderDetail(orderDetail)
                .refundAmount(1000L)
                .quantity(1L)
                .statusCode(ORDER_PARTIAL_CANCEL.getCode())
                .reason("단순변심")
                .build();

        testContainer.orderCancelReturnCommandRepository.save(returnDetail);
        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);
        testContainer.memberRepository.save(member);

        Long orderReturnId = 1L;
        Long memberId = 1L;

        // when
        ApiResponse<GetReturnDetailHttp.Response> result = testContainer.orderReturnController.getReturnDetail(orderReturnId, memberId);

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
    @DisplayName("반품 상세 조회 (BASIC_Coupon)")
    public void getReturnDetail_BASIC_Coupon() {
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
        ApiResponse<GetReturnDetailHttp.Response> result = testContainer.orderReturnController.getReturnDetail(orderCancelId, memberId);

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
    public void getReturnDetail_NoMember_ERROR() {
        // given
        Long orderDetailId = 1L;
        Long memberId = 1L;

        // when // then
        assertThatThrownBy(() -> testContainer.orderReturnController.getReturnDetail(orderDetailId, memberId))
                .isInstanceOf(JpaObjectRetrievalFailureException.class);
    }

    @Test
    @DisplayName("주문 상세가 없을 경우 에러가 발생한다.")
    public void getReturnDetail_ENTITY_NOT_FOUND_ERROR() {
        // given
        Member member = Member.builder().build();

        testContainer.memberRepository.save(member);

        Long orderDetailId = 1L;
        Long memberId = 1L;

        // when
        assertThatThrownBy(() -> testContainer.orderReturnController.getReturnDetail(orderDetailId, memberId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");
    }
}