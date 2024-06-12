package com.objects.marketbridge.domains.order.service.dto;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.QUANTITY_EXCEEDED;
import static com.objects.marketbridge.domains.order.domain.MemberShipPrice.BASIC;
import static com.objects.marketbridge.domains.order.domain.MemberShipPrice.WOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class RequestCancelDtoTest {

    @Test
    @DisplayName("Response 변환할 수 있다. (BASIC and NoCoupon)")
    public void response_of_BASIC_and_NoCoupon() {
        // given
        String memberShip = MembershipType.BASIC.getText();

        Product product = Product.builder()
                .name("빵빵이")
                .thumbImg("빵빵이 이미지")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(0L)
                .product(product)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfCancellation = 2L;

        // when
        RequestCancelDto.Response result = RequestCancelDto.Response.of(orderDetail, numberOfCancellation, memberShip);

        // then
        assertThat(result.getProductInfo())
                .extracting("quantity", "name", "price", "image")
                .contains(2L, "빵빵이", 1000L, "빵빵이 이미지");

        assertThat(result.getCancelRefundInfo())
                .extracting("deliveryFee", "refundFee", "discountPrice", "totalPrice")
                .contains(BASIC.getDeliveryFee(), BASIC.getRefundFee(), 0L, 2000L);
    }

    @Test
    @DisplayName("Response 변환할 수 있다. (BASIC and Coupon)")
    public void response_of_BASIC_and_Coupon() {
        // given
        String memberShip = MembershipType.BASIC.getText();

        Product product = Product.builder()
                .name("빵빵이")
                .thumbImg("빵빵이 이미지")
                .build();

        Coupon coupon = Coupon.builder()
                .product(product)
                .price(1000L)
                .build();

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(0L)
                .memberCoupon(memberCoupon)
                .product(product)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfCancellation = 2L;

        // when
        RequestCancelDto.Response result = RequestCancelDto.Response.of(orderDetail, numberOfCancellation, memberShip);

        // then
        assertThat(result.getProductInfo())
                .extracting("quantity", "name", "price", "image")
                .contains(2L, "빵빵이", 1000L, "빵빵이 이미지");

        assertThat(result.getCancelRefundInfo())
                .extracting("deliveryFee", "refundFee", "discountPrice", "totalPrice")
                .contains(BASIC.getDeliveryFee(), BASIC.getRefundFee(), 1000L, 2000L);
    }

    @Test
    @DisplayName("Response 변환할 수 있다. (WOW and NoCoupon)")
    public void response_of_WOW_and_NoCoupon() {
        // given
        String memberShip = MembershipType.WOW.getText();

        Product product = Product.builder()
                .name("빵빵이")
                .thumbImg("빵빵이 이미지")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .product(product)
                .reducedQuantity(0L)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfCancellation = 2L;

        // when
        RequestCancelDto.Response result = RequestCancelDto.Response.of(orderDetail, numberOfCancellation, memberShip);

        // then
        assertThat(result.getProductInfo())
                .extracting("quantity", "name", "price", "image")
                .contains(2L, "빵빵이", 1000L, "빵빵이 이미지");

        assertThat(result.getCancelRefundInfo())
                .extracting("deliveryFee", "refundFee", "discountPrice", "totalPrice")
                .contains(WOW.getDeliveryFee(), WOW.getRefundFee(), 0L, 2000L);
    }

    @Test
    @DisplayName("Response 변환할 수 있다. (WOW and Coupon)")
    public void response_of_WOW_and_Coupon() {
        // given
        String memberShip = MembershipType.WOW.getText();

        Product product = Product.builder()
                .name("빵빵이")
                .thumbImg("빵빵이 이미지")
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
                .reducedQuantity(0L)
                .product(product)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfCancellation = 2L;

        // when
        RequestCancelDto.Response result = RequestCancelDto.Response.of(orderDetail, numberOfCancellation, memberShip);

        // then
        assertThat(result.getProductInfo())
                .extracting("quantity", "name", "price", "image")
                .contains(2L, "빵빵이", 1000L, "빵빵이 이미지");

        assertThat(result.getCancelRefundInfo())
                .extracting("deliveryFee", "refundFee", "discountPrice", "totalPrice")
                .contains(WOW.getDeliveryFee(), WOW.getRefundFee(), 1000L, 2000L);
    }

    @Test
    @DisplayName("상품 취소 수량이 주문 상세 수량보다 많을경우 에러를 던진다.")
    public void response_of_Error() {
        // given
        String memberShip = MembershipType.WOW.getText();

        Product product = Product.builder()
                .name("빵빵이")
                .thumbImg("빵빵이 이미지")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .product(product)
                .reducedQuantity(0L)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfCancellation = 4L;

        // when // then
        assertThatThrownBy(() -> RequestCancelDto.Response.of(orderDetail, numberOfCancellation, memberShip))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("수량이 초과 되었습니다.")
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(QUANTITY_EXCEEDED);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                });
    }

    @Test
    @DisplayName("주문 상세를 ProductInfo로 변환")
    public void productInfo_of() {
        // given
        Product product = Product.builder()
                .name("빵빵이")
                .thumbImg("빵빵이 이미지")
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(0L)
                .product(product)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfCancellation = 2L;

        // when
        RequestCancelDto.ProductInfo result = RequestCancelDto.ProductInfo.of(orderDetail, numberOfCancellation);

        // then
        assertThat(result).extracting("quantity", "name", "price", "image")
                .contains(2L, "빵빵이", 1000L, "빵빵이 이미지");
    }

    @Test
    @DisplayName("주문 상세 리스트와 멤버십이 주어지면 CancelRefundInfo를 반환한다.(BASIC and NoCoupon)")
    public void CancelRefundInfo_of_BASIC_and_NoCoupon() {
        // given
        String memberShip = MembershipType.BASIC.getText();

        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(0L)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfCancellation = 2L;

        // when
        RequestCancelDto.CancelRefundInfo result = RequestCancelDto.CancelRefundInfo.of(orderDetail, memberShip, numberOfCancellation);

        // then
        assertThat(result)
                .extracting("deliveryFee", "refundFee", "discountPrice", "totalPrice")
                .contains(BASIC.getDeliveryFee(), BASIC.getRefundFee(), 0L, 2000L);
    }

    @Test
    @DisplayName("주문 상세 리스트와 멤버십이 주어지면 CancelRefundInfo를 반한다.(BASIC and Coupon)")
    public void CancelRefundInfo_of_BASIC_and_Coupon() {
        // given
        String memberShip = MembershipType.BASIC.getText();

        Coupon coupon = Coupon.builder()
                .price(1000L)
                .build();

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(0L)
                .memberCoupon(memberCoupon)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfCancellation = 2L;

        // when
        RequestCancelDto.CancelRefundInfo result = RequestCancelDto.CancelRefundInfo.of(orderDetail, memberShip, numberOfCancellation);

        // then
        assertThat(result)
                .extracting("deliveryFee", "refundFee", "discountPrice", "totalPrice")
                .contains(BASIC.getDeliveryFee(), BASIC.getRefundFee(), 1000L, 2000L);
    }

    @Test
    @DisplayName("주문 상세 리스트와 멤버십이 주어지면 CancelRefundInfo를 반한다.(WOW and NoCoupon)")
    public void CancelRefundInfo_of_WOW_and_NoCoupon() {
        // given
        String memberShip = MembershipType.WOW.getText();

        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(0L)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfCancellation = 2L;

        // when
        RequestCancelDto.CancelRefundInfo result = RequestCancelDto.CancelRefundInfo.of(orderDetail, memberShip, numberOfCancellation);

        // then
        assertThat(result)
                .extracting("deliveryFee", "refundFee", "discountPrice", "totalPrice")
                .contains(WOW.getDeliveryFee(), WOW.getRefundFee(), 0L, 2000L);
    }

    @Test
    @DisplayName("주문 상세 리스트와 멤버십이 주어지면 CancelRefundInfo를 반한다.(WOW and Coupon)")
    public void CancelRefundInfo_of_WOW_and_Coupon() {
        // given
        String memberShip = MembershipType.WOW.getText();

        Coupon coupon = Coupon.builder()
                .price(1000L)
                .build();

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(0L)
                .memberCoupon(memberCoupon)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfCancellation = 2L;

        // when
        RequestCancelDto.CancelRefundInfo result = RequestCancelDto.CancelRefundInfo.of(orderDetail, memberShip, numberOfCancellation);

        // then
        assertThat(result)
                .extracting("deliveryFee", "refundFee", "discountPrice", "totalPrice")
                .contains(WOW.getDeliveryFee(), WOW.getRefundFee(), 1000L, 2000L);
    }

    @Test
    @DisplayName("상품 취소 수량이 주문 상세 수량보다 많을경우 에러를 던진다.")
    public void CancelRefundInfo_of_Error() {
        // given
        String memberShip = MembershipType.WOW.getText();

        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(0L)
                .quantity(3L)
                .build();

        Long numberOfCancellation = 4L;

        // when // then
        assertThatThrownBy(() -> RequestCancelDto.CancelRefundInfo.of(orderDetail, memberShip, numberOfCancellation))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("수량이 초과 되었습니다.")
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(QUANTITY_EXCEEDED);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                });

    }

}