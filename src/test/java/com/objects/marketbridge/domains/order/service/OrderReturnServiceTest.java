package com.objects.marketbridge.domains.order.service;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.order.domain.MemberShipPrice;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.mock.*;
import com.objects.marketbridge.domains.order.service.dto.ConfirmReturnDto;
import com.objects.marketbridge.domains.order.service.dto.GetReturnDetailDto;
import com.objects.marketbridge.domains.order.service.dto.RequestReturnDto;
import com.objects.marketbridge.domains.product.domain.Product;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.NON_RETURNABLE_PRODUCT;
import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.QUANTITY_EXCEEDED;
import static com.objects.marketbridge.domains.member.domain.MembershipType.BASIC;
import static com.objects.marketbridge.domains.member.domain.MembershipType.WOW;
import static com.objects.marketbridge.domains.order.domain.StatusCodeType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class OrderReturnServiceTest {

    private LocalDateTime orderDate = LocalDateTime.of(2024, 2, 9, 3, 9);
    private LocalDateTime now = LocalDateTime.of(2024, 2, 11, 3, 49);
    private TestContainer testContainer = TestContainer.builder()
            .dateTimeHolder(
                    TestDateTimeHolder.builder()
                            .now(now)
                            .createTime(orderDate)
                            .build()
            )
            .build();

    @AfterEach
    void afterEach() {
        BaseFakeOrderDetailRepository.getInstance().clear();
        BaseFakeOrderRepository.getInstance().clear();
        BaseFakeOrderCancelReturnRepository.getInstance().clear();
    }

    @Test
    @DisplayName("저장된 주문상세가 없으면 오류가 발생한다.")
    public void confirmReturn_NoOrderDetail_ERROR() {
        // given
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder().build();

        ConfirmReturnDto.Request request = ConfirmReturnDto.Request.builder()
                .orderDetailId(1L)
                .build();

        // when // then
        assertThatThrownBy(() -> testContainer.orderReturnService.confirmReturn(request, dateTimeHolder))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");
    }

    @Test
    @DisplayName("배송완료된 상품이 아니면 에러를 발생시킨다.")
    public void confirmReturn_NO_DELIVERY_COMPLETED_ERROR() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 10, 51);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        ConfirmReturnDto.Request request = ConfirmReturnDto.Request.builder()
                .orderDetailId(1L)
                .numberOfReturns(2L)
                .reason("단순변심")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .statusCode(PAYMENT_PENDING.getCode())
                .build();

        testContainer.orderDetailCommandRepository.save(orderDetail);

        // when // then
        assertThatThrownBy(() -> testContainer.orderReturnService.confirmReturn(request, dateTimeHolder))
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
        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 10, 51);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        ConfirmReturnDto.Request request = ConfirmReturnDto.Request.builder()
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
        assertThatThrownBy(() -> testContainer.orderReturnService.confirmReturn(request, dateTimeHolder))
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
    @DisplayName("부분 반품이면 상품재고, 줄어든 수량이 변경된다.(NoCoupon)")
    public void confirmReturn_Partial_NoCoupon() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 10, 51);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        ConfirmReturnDto.Request request = ConfirmReturnDto.Request.builder()
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
        ConfirmReturnDto.Response result = testContainer.orderReturnService.confirmReturn(request, dateTimeHolder);

        // then
        assertThat(orderDetail.getStatusCode()).isEqualTo(RETURN_INIT.getCode());
        assertThat(product.getStock()).isEqualTo(7L);
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getTotalPrice()).isEqualTo(2000L);
        assertThat(result.getReturnedDate()).isEqualTo(cancelledAt);
        assertThat(result.getReturnedItem().getProductId()).isEqualTo(1L);
        assertThat(result.getReturnedItem().getProductNo()).isEqualTo("1");
        assertThat(result.getReturnedItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getReturnedItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getReturnedItem().getQuantity()).isEqualTo(2L);
        assertThat(result.getRefundInfo().getTotalRefundAmount()).isEqualTo(2000L);
        assertThat(result.getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("모두 반품이면 상품재고, 줄어든 수량, 상태코드, 이유가 변경된다.(NoCoupon)")
    public void confirmReturn_All_NoCoupon() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 10, 51);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        ConfirmReturnDto.Request request = ConfirmReturnDto.Request.builder()
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
        ConfirmReturnDto.Response result = testContainer.orderReturnService.confirmReturn(request, dateTimeHolder);

        // then
        assertThat(orderDetail.getStatusCode()).isEqualTo(RETURN_INIT.getCode());
        assertThat(product.getStock()).isEqualTo(8L);
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getTotalPrice()).isEqualTo(3000L);
        assertThat(result.getReturnedDate()).isEqualTo(cancelledAt);
        assertThat(result.getReturnedItem().getProductId()).isEqualTo(1L);
        assertThat(result.getReturnedItem().getProductNo()).isEqualTo("1");
        assertThat(result.getReturnedItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getReturnedItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getReturnedItem().getQuantity()).isEqualTo(3L);
        assertThat(result.getRefundInfo().getTotalRefundAmount()).isEqualTo(3000L);
        assertThat(result.getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("부분 반품이면 상품재고, 줄어든 수량이 변경된다.(Coupon)")
    public void confirmReturn_Partial_Coupon() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 10, 51);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        ConfirmReturnDto.Request request = ConfirmReturnDto.Request.builder()
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
        ConfirmReturnDto.Response result = testContainer.orderReturnService.confirmReturn(request, dateTimeHolder);

        // then
        assertThat(memberCoupon.getIsUsed()).isFalse();
        assertThat(memberCoupon.getUsedDate()).isNull();
        assertThat(orderDetail.getStatusCode()).isEqualTo(RETURN_INIT.getCode());
        assertThat(product.getStock()).isEqualTo(7L);
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getTotalPrice()).isEqualTo(2000L);
        assertThat(result.getReturnedDate()).isEqualTo(cancelledAt);
        assertThat(result.getReturnedItem().getProductId()).isEqualTo(1L);
        assertThat(result.getReturnedItem().getProductNo()).isEqualTo("1");
        assertThat(result.getReturnedItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getReturnedItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getReturnedItem().getQuantity()).isEqualTo(2L);
        assertThat(result.getRefundInfo().getTotalRefundAmount()).isEqualTo(2000L);
        assertThat(result.getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("모두 반품이면 상품재고, 줄어든 수량, 상태코드, 이유가 변경된다.(Coupon)")
    public void confirmReturn_All_Coupon() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 10, 51);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        ConfirmReturnDto.Request request = ConfirmReturnDto.Request.builder()
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
        ConfirmReturnDto.Response result = testContainer.orderReturnService.confirmReturn(request, dateTimeHolder);

        // then
        assertThat(memberCoupon.getIsUsed()).isFalse();
        assertThat(memberCoupon.getUsedDate()).isNull();
        assertThat(orderDetail.getStatusCode()).isEqualTo(RETURN_INIT.getCode());
        assertThat(product.getStock()).isEqualTo(8L);
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getTotalPrice()).isEqualTo(3000L);
        assertThat(result.getReturnedDate()).isEqualTo(cancelledAt);
        assertThat(result.getReturnedItem().getProductId()).isEqualTo(1L);
        assertThat(result.getReturnedItem().getProductNo()).isEqualTo("1");
        assertThat(result.getReturnedItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getReturnedItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getReturnedItem().getQuantity()).isEqualTo(3L);
        assertThat(result.getRefundInfo().getTotalRefundAmount()).isEqualTo(3000L);
        assertThat(result.getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getRefundInfo().getRefundProcessedAt()).isEqualTo(this.now);
    }

    @Test
    @DisplayName("주문 취소 요청 (WOW)")
    public void findReturnInfo_WOW() {
        // given
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

        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfReturns = 2L;
        String membership = WOW.getText();

        // when
        RequestReturnDto.Response result = testContainer.orderReturnService.findReturnInfo(orderDetailId, numberOfReturns, membership);
        RequestReturnDto.ReturnRefundInfo returnRefundInfo = result.getReturnRefundInfo();
        RequestReturnDto.ProductInfo productInfo = result.getProductInfo();

        // then
        assertThat(returnRefundInfo.getDeliveryFee()).isEqualTo(MemberShipPrice.WOW.getDeliveryFee());
        assertThat(returnRefundInfo.getReturnFee()).isEqualTo(MemberShipPrice.WOW.getReturnFee());
        assertThat(returnRefundInfo.getProductTotalPrice()).isEqualTo(2000L);

        assertThat(productInfo.getQuantity()).isEqualTo(2L);
        assertThat(productInfo.getName()).isEqualTo("빵빵이키링");
        assertThat(productInfo.getPrice()).isEqualTo(1000L);
        assertThat(productInfo.getImage()).isEqualTo("빵빵이썸네일");
    }

    @Test
    @DisplayName("주문 반품 요청 (BASIC)")
    public void findReturnInfo_BASIC() {
        // given
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

        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfReturns = 2L;
        String membership = BASIC.getText();

        // when
        RequestReturnDto.Response result = testContainer.orderReturnService.findReturnInfo(orderDetailId, numberOfReturns, membership);
        RequestReturnDto.ReturnRefundInfo returnRefundInfo = result.getReturnRefundInfo();
        RequestReturnDto.ProductInfo productInfo = result.getProductInfo();

        // then
        assertThat(returnRefundInfo.getDeliveryFee()).isEqualTo(MemberShipPrice.BASIC.getDeliveryFee());
        assertThat(returnRefundInfo.getReturnFee()).isEqualTo(MemberShipPrice.BASIC.getReturnFee());
        assertThat(returnRefundInfo.getProductTotalPrice()).isEqualTo(2000L);

        assertThat(productInfo.getQuantity()).isEqualTo(2L);
        assertThat(productInfo.getName()).isEqualTo("빵빵이키링");
        assertThat(productInfo.getPrice()).isEqualTo(1000L);
        assertThat(productInfo.getImage()).isEqualTo("빵빵이썸네일");
    }

    @Test
    @DisplayName("주문 상세가 없을 경우 에러가 발생한다.")
    public void findReturnInfo_ENTITY_NOT_FOUND_ERROR() {
        // given
        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        String membership = BASIC.getText();

        // when
        assertThatThrownBy(() -> testContainer.orderReturnService.findReturnInfo(orderDetailId, numberOfCancellation, membership))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");
    }

    @Test
    @DisplayName("배송 완료된 상품은 취소가 불가능하다.")
    public void findReturnInfo_NON_RETURNABLE_PRODUCT_ERROR() {
        // given
        Product product = Product.builder()
                .name("빵빵이키링")
                .thumbImg("빵빵이썸네일")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .quantity(10L)
                .product(product)
                .price(1000L)
                .statusCode(RELEASE_PENDING.getCode())
                .build();

        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        String membership = BASIC.getText();

        // when
        assertThatThrownBy(() -> testContainer.orderReturnService.findReturnInfo(orderDetailId, numberOfCancellation, membership))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("반품이 불가능한 상품입니다.")
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(NON_RETURNABLE_PRODUCT);
                });
    }

    @Test
    @DisplayName("반품 상세 조회 (WOW_NoCoupon)")
    public void findReturnDetail_WOW_NoCoupon() {
        // given
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

        Long orderReturnId = 1L;
        String membership = WOW.getText();

        // when
        GetReturnDetailDto.Response result = testContainer.orderReturnService.findReturnDetail(orderReturnId, membership);

        // then
        assertThat(result.getOrderDate()).isEqualTo(orderDate);
        assertThat(result.getCancelDate()).isEqualTo(cancelledAt);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getReason()).isEqualTo("단순변심");

        assertThat(result.getProductInfo().getProductId()).isEqualTo(1L);
        assertThat(result.getProductInfo().getProductNo()).isEqualTo("1");
        assertThat(result.getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getProductInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getProductInfo().getQuantity()).isEqualTo(10L);

        assertThat(result.getRefundInfo().getDeliveryFee()).isEqualTo(MemberShipPrice.WOW.getDeliveryFee());
        assertThat(result.getRefundInfo().getRefundFee()).isEqualTo(MemberShipPrice.WOW.getRefundFee());
        assertThat(result.getRefundInfo().getDiscountPrice()).isEqualTo(0L);
        assertThat(result.getRefundInfo().getTotalPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("반품 상세 조회 (BASIC_NoCoupon)")
    public void findReturnDetail_BASIC_NoCoupon() {
        // given
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

        Long orderReturnId = 1L;
        String membership = BASIC.getText();

        // when
        GetReturnDetailDto.Response result = testContainer.orderReturnService.findReturnDetail(orderReturnId, membership);

        // then
        assertThat(result.getOrderDate()).isEqualTo(orderDate);
        assertThat(result.getCancelDate()).isEqualTo(cancelledAt);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getReason()).isEqualTo("단순변심");

        assertThat(result.getProductInfo().getProductId()).isEqualTo(1L);
        assertThat(result.getProductInfo().getProductNo()).isEqualTo("1");
        assertThat(result.getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getProductInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getProductInfo().getQuantity()).isEqualTo(10L);

        assertThat(result.getRefundInfo().getDeliveryFee()).isEqualTo(MemberShipPrice.BASIC.getDeliveryFee());
        assertThat(result.getRefundInfo().getRefundFee()).isEqualTo(MemberShipPrice.BASIC.getRefundFee());
        assertThat(result.getRefundInfo().getDiscountPrice()).isEqualTo(0L);
        assertThat(result.getRefundInfo().getTotalPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("반품 상세 조회 (WOW_Coupon)")
    public void findReturnDetail_WOW_Coupon() {
        // given
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

        Long orderReturnId = 1L;
        String membership = WOW.getText();

        // when
        GetReturnDetailDto.Response result = testContainer.orderReturnService.findReturnDetail(orderReturnId, membership);

        // then
        assertThat(result.getOrderDate()).isEqualTo(orderDate);
        assertThat(result.getCancelDate()).isEqualTo(cancelledAt);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getReason()).isEqualTo("단순변심");

        assertThat(result.getProductInfo().getProductId()).isEqualTo(1L);
        assertThat(result.getProductInfo().getProductNo()).isEqualTo("1");
        assertThat(result.getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getProductInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getProductInfo().getQuantity()).isEqualTo(10L);

        assertThat(result.getRefundInfo().getDeliveryFee()).isEqualTo(MemberShipPrice.WOW.getDeliveryFee());
        assertThat(result.getRefundInfo().getRefundFee()).isEqualTo(MemberShipPrice.WOW.getRefundFee());
        assertThat(result.getRefundInfo().getDiscountPrice()).isEqualTo(1000L);
        assertThat(result.getRefundInfo().getTotalPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("반품 상세 조회 (BASIC_Coupon)")
    public void findReturnDetail_BASIC_Coupon() {
        // given
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

        Long orderReturnId = 1L;
        String membership = BASIC.getText();

        // when
        GetReturnDetailDto.Response result = testContainer.orderReturnService.findReturnDetail(orderReturnId, membership);

        // then
        assertThat(result.getOrderDate()).isEqualTo(orderDate);
        assertThat(result.getCancelDate()).isEqualTo(cancelledAt);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getReason()).isEqualTo("단순변심");

        assertThat(result.getProductInfo().getProductId()).isEqualTo(1L);
        assertThat(result.getProductInfo().getProductNo()).isEqualTo("1");
        assertThat(result.getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getProductInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getProductInfo().getQuantity()).isEqualTo(10L);

        assertThat(result.getRefundInfo().getDeliveryFee()).isEqualTo(MemberShipPrice.BASIC.getDeliveryFee());
        assertThat(result.getRefundInfo().getRefundFee()).isEqualTo(MemberShipPrice.BASIC.getRefundFee());
        assertThat(result.getRefundInfo().getDiscountPrice()).isEqualTo(1000L);
        assertThat(result.getRefundInfo().getTotalPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("반품 상세가 없을 경우 에러가 발생한다.")
    public void findReturnDetail_ENTITY_NOT_FOUND_ERROR() {
        // given
        Long orderDetailId = 1L;
        String membership = BASIC.getText();

        // when
        assertThatThrownBy(() -> testContainer.orderReturnService.findReturnDetail(orderDetailId, membership))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");
    }

}