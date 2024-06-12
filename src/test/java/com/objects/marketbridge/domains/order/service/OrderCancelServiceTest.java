package com.objects.marketbridge.domains.order.service;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.order.domain.MemberShipPrice;
import com.objects.marketbridge.domains.order.domain.OrderCancelReturn;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.mock.*;
import com.objects.marketbridge.domains.order.service.dto.ConfirmCancelDto;
import com.objects.marketbridge.domains.order.service.dto.GetCancelDetailDto;
import com.objects.marketbridge.domains.order.service.dto.RequestCancelDto;
import com.objects.marketbridge.domains.product.domain.Product;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.NON_CANCELLABLE_PRODUCT;
import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.QUANTITY_EXCEEDED;
import static com.objects.marketbridge.domains.member.domain.MembershipType.BASIC;
import static com.objects.marketbridge.domains.member.domain.MembershipType.WOW;
import static com.objects.marketbridge.domains.order.domain.StatusCodeType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class OrderCancelServiceTest {

    private LocalDateTime orderDate = LocalDateTime.of(2024, 2, 9, 3, 9);
    private DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
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
    }

    @Test
    @DisplayName("저장된 주문상세가 없으면 오류가 발생한다.")
    public void confirmCancel_NoOrderDetail_ERROR() {
        // given
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder().build();

        ConfirmCancelDto.Request request = ConfirmCancelDto.Request.builder()
                .orderDetailId(1L)
                .build();

        // when // then
        assertThatThrownBy(() -> testContainer.orderCancelService.confirmCancel(request, dateTimeHolder))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");
    }

    @Test
    @DisplayName("배송완료된 주문은 취소할 수 없다.")
    public void confirmCancel_DELIVERY_COMPLETED_ERROR() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 10, 51);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        ConfirmCancelDto.Request request = ConfirmCancelDto.Request.builder()
                .orderDetailId(1L)
                .numberOfCancellation(2L)
                .reason("단순변심")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .statusCode(DELIVERY_COMPLETED.getCode())
                .build();

        testContainer.orderDetailCommandRepository.save(orderDetail);

        // when // then
        assertThatThrownBy(() -> testContainer.orderCancelService.confirmCancel(request, dateTimeHolder))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("취소가 불가능한 상품입니다.")
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
        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 10, 51);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        ConfirmCancelDto.Request request = ConfirmCancelDto.Request.builder()
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
        assertThatThrownBy(() -> testContainer.orderCancelService.confirmCancel(request, dateTimeHolder))
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
    @DisplayName("주문 취소 요청 (WOW_NoCoupon)")
    public void findCancelInfo_WOW_NoCoupon() {
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
                .statusCode(ORDER_RECEIVED.getCode())
                .build();

        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        String membership = WOW.getText();

        // when
        RequestCancelDto.Response result = testContainer.orderCancelService.findCancelInfo(orderDetailId, numberOfCancellation, membership);
        RequestCancelDto.CancelRefundInfo cancelRefundInfo = result.getCancelRefundInfo();
        RequestCancelDto.ProductInfo productInfo = result.getProductInfo();

        // then
        assertThat(cancelRefundInfo.getDeliveryFee()).isEqualTo(MemberShipPrice.WOW.getDeliveryFee());
        assertThat(cancelRefundInfo.getRefundFee()).isEqualTo(MemberShipPrice.WOW.getRefundFee());
        assertThat(cancelRefundInfo.getDiscountPrice()).isEqualTo(0L);
        assertThat(cancelRefundInfo.getTotalPrice()).isEqualTo(2000L);

        assertThat(productInfo.getQuantity()).isEqualTo(2L);
        assertThat(productInfo.getName()).isEqualTo("빵빵이키링");
        assertThat(productInfo.getPrice()).isEqualTo(1000L);
        assertThat(productInfo.getImage()).isEqualTo("빵빵이썸네일");
    }

    @Test
    @DisplayName("주문 취소 요청 (BASIC_NoCoupon)")
    public void findCancelInfo_BASIC_NoCoupon() {
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
                .statusCode(ORDER_RECEIVED.getCode())
                .build();

        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        String membership = BASIC.getText();

        // when
        RequestCancelDto.Response result = testContainer.orderCancelService.findCancelInfo(orderDetailId, numberOfCancellation, membership);
        RequestCancelDto.CancelRefundInfo cancelRefundInfo = result.getCancelRefundInfo();
        RequestCancelDto.ProductInfo productInfo = result.getProductInfo();

        // then
        assertThat(cancelRefundInfo.getDeliveryFee()).isEqualTo(MemberShipPrice.BASIC.getDeliveryFee());
        assertThat(cancelRefundInfo.getRefundFee()).isEqualTo(MemberShipPrice.BASIC.getRefundFee());
        assertThat(cancelRefundInfo.getDiscountPrice()).isEqualTo(0L);
        assertThat(cancelRefundInfo.getTotalPrice()).isEqualTo(2000L);

        assertThat(productInfo.getQuantity()).isEqualTo(2L);
        assertThat(productInfo.getName()).isEqualTo("빵빵이키링");
        assertThat(productInfo.getPrice()).isEqualTo(1000L);
        assertThat(productInfo.getImage()).isEqualTo("빵빵이썸네일");
    }

    @Test
    @DisplayName("주문 취소 요청 (WOW_Coupon)")
    public void findCancelInfo_WOW_Coupon() {
        // given
        Product product = Product.builder()
                .name("빵빵이키링")
                .thumbImg("빵빵이썸네일")
                .build();

        Coupon coupon = Coupon.builder()
                .product(product)
                .price(1000L)
                .build();

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(memberCoupon)
                .quantity(10L)
                .reducedQuantity(0L)
                .product(product)
                .price(1000L)
                .statusCode(ORDER_RECEIVED.getCode())
                .build();

        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        String membership = WOW.getText();

        // when
        RequestCancelDto.Response result = testContainer.orderCancelService.findCancelInfo(orderDetailId, numberOfCancellation, membership);
        RequestCancelDto.CancelRefundInfo cancelRefundInfo = result.getCancelRefundInfo();
        RequestCancelDto.ProductInfo productInfo = result.getProductInfo();

        // then
        assertThat(cancelRefundInfo.getDeliveryFee()).isEqualTo(MemberShipPrice.WOW.getDeliveryFee());
        assertThat(cancelRefundInfo.getRefundFee()).isEqualTo(MemberShipPrice.WOW.getRefundFee());
        assertThat(cancelRefundInfo.getDiscountPrice()).isEqualTo(1000L);
        assertThat(cancelRefundInfo.getTotalPrice()).isEqualTo(2000L);

        assertThat(productInfo.getQuantity()).isEqualTo(2L);
        assertThat(productInfo.getName()).isEqualTo("빵빵이키링");
        assertThat(productInfo.getPrice()).isEqualTo(1000L);
        assertThat(productInfo.getImage()).isEqualTo("빵빵이썸네일");
    }

    @Test
    @DisplayName("주문 취소 요청 (BASIC_Coupon)")
    public void findCancelInfo_BASIC_Coupon() {
        // given
        Product product = Product.builder()
                .name("빵빵이키링")
                .thumbImg("빵빵이썸네일")
                .build();

        Coupon coupon = Coupon.builder()
                .product(product)
                .price(1000L)
                .build();

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(memberCoupon)
                .quantity(10L)
                .product(product)
                .reducedQuantity(0L)
                .price(1000L)
                .statusCode(ORDER_RECEIVED.getCode())
                .build();

        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        String membership = BASIC.getText();

        // when
        RequestCancelDto.Response result = testContainer.orderCancelService.findCancelInfo(orderDetailId, numberOfCancellation, membership);
        RequestCancelDto.CancelRefundInfo cancelRefundInfo = result.getCancelRefundInfo();
        RequestCancelDto.ProductInfo productInfo = result.getProductInfo();

        // then
        assertThat(cancelRefundInfo.getDeliveryFee()).isEqualTo(MemberShipPrice.BASIC.getDeliveryFee());
        assertThat(cancelRefundInfo.getRefundFee()).isEqualTo(MemberShipPrice.BASIC.getRefundFee());
        assertThat(cancelRefundInfo.getDiscountPrice()).isEqualTo(1000L);
        assertThat(cancelRefundInfo.getTotalPrice()).isEqualTo(2000L);

        assertThat(productInfo.getQuantity()).isEqualTo(2L);
        assertThat(productInfo.getName()).isEqualTo("빵빵이키링");
        assertThat(productInfo.getPrice()).isEqualTo(1000L);
        assertThat(productInfo.getImage()).isEqualTo("빵빵이썸네일");
    }

    @Test
    @DisplayName("주문 상세가 없을 경우 에러가 발생한다.")
    public void findCancelInfo_ENTITY_NOT_FOUND_ERROR() {
        // given
        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        String membership = BASIC.getText();

        // when
        assertThatThrownBy(() -> testContainer.orderCancelService.findCancelInfo(orderDetailId, numberOfCancellation, membership))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");
    }

    @Test
    @DisplayName("배송 완료된 상품은 취소가 불가능하다.")
    public void findCancelInfo_NON_CANCELLABLE_PRODUCT_ERROR() {
        // given
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

        testContainer.productRepository.save(product);
        testContainer.orderDetailCommandRepository.save(orderDetail);

        Long orderDetailId = 1L;
        Long numberOfCancellation = 2L;
        String membership = BASIC.getText();

        // when
        assertThatThrownBy(() -> testContainer.orderCancelService.findCancelInfo(orderDetailId, numberOfCancellation, membership))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("취소가 불가능한 상품입니다.")
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(NON_CANCELLABLE_PRODUCT);
                });
    }

    @Test
    @DisplayName("취소 상세 조회 (WOW_NoCoupon)")
    public void findCancelDetail_WOW_NoCoupon() {
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

        Long orderCancelId = 1L;
        String membership = WOW.getText();

        // when
        GetCancelDetailDto.Response result = testContainer.orderCancelService.findCancelDetail(orderCancelId, membership);

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
    @DisplayName("취소 상세 조회 (BASIC_NoCoupon)")
    public void findCancelDetail_BASIC_NoCoupon() {
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

        Long orderCancelId = 1L;
        String membership = BASIC.getText();

        // when
        GetCancelDetailDto.Response result = testContainer.orderCancelService.findCancelDetail(orderCancelId, membership);

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
    @DisplayName("취소 상세 조회 (WOW_Coupon)")
    public void findCancelDetail_WOW_Coupon() {
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

        Long orderCancelId = 1L;
        String membership = WOW.getText();

        // when
        GetCancelDetailDto.Response result = testContainer.orderCancelService.findCancelDetail(orderCancelId, membership);

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
    @DisplayName("취소 상세 조회 (BASIC_Coupon)")
    public void findCancelDetail_BASIC_Coupon() {
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

        Long orderCancelId = 1L;
        String membership = BASIC.getText();

        // when
        GetCancelDetailDto.Response result = testContainer.orderCancelService.findCancelDetail(orderCancelId, membership);

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
    @DisplayName("주문 상세가 없을 경우 에러가 발생한다.")
    public void findCancelDetail_ENTITY_NOT_FOUND_ERROR() {
        // given
        Long orderDetailId = 1L;
        String membership = BASIC.getText();

        // when
        assertThatThrownBy(() -> testContainer.orderCancelService.findCancelDetail(orderDetailId, membership))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("엔티티가 존재하지 않습니다");
    }

}