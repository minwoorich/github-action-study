package com.objects.marketbridge.domains.order.service.dto;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
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

class RequestReturnDtoTest {

    @Test
    @DisplayName("주문 상세 리스트와 맴버십이 주어진 경우 Response를 반환한다.(BASIC)")
    public void response_of_BASIC() {
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

        Long numberOfReturns = 2L;

        // when
        RequestReturnDto.Response result = RequestReturnDto.Response.of(orderDetail, numberOfReturns, memberShip);

        // then
        assertThat(result.getProductInfo())
                .extracting("quantity", "name", "price", "image")
                .contains(2L, "빵빵이", 1000L, "빵빵이 이미지");

        assertThat(result.getReturnRefundInfo())
                .extracting("deliveryFee", "returnFee", "productTotalPrice")
                .contains(BASIC.getDeliveryFee(), BASIC.getReturnFee(), 2000L);

    }

    @Test
    @DisplayName("주문 상세 리스트와 맴버십이 주어진 경우 Response를 반환한다.(WOW)")
    public void response_of_WOW() {
        // given
        String memberShip = MembershipType.WOW.getText();

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

        Long numberOfReturns = 2L;

        // when
        RequestReturnDto.Response result = RequestReturnDto.Response.of(orderDetail, numberOfReturns, memberShip);

        // then
        assertThat(result.getProductInfo())
                .extracting("quantity", "name", "price", "image")
                .contains(2L, "빵빵이", 1000L, "빵빵이 이미지");

        assertThat(result.getReturnRefundInfo())
                .extracting("deliveryFee", "returnFee", "productTotalPrice")
                .contains(WOW.getDeliveryFee(), WOW.getReturnFee(), 2000L);

    }

    @Test
    @DisplayName("반품 수량이 주문 상세 수량보다 많을경우 에러를 던진다.")
    public void response_of_ERROR() {
        // given
        String memberShip = MembershipType.WOW.getText();

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

        Long numberOfReturns = 4L;

        // when // then
        assertThatThrownBy(() -> RequestReturnDto.Response.of(orderDetail, numberOfReturns, memberShip))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("수량이 초과 되었습니다.")
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(QUANTITY_EXCEEDED);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                });

    }

    @Test
    @DisplayName("주문 상세를 ProductInfo로 반환한다.")
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

        Long numberOfReturns = 2L;

        // when
        RequestReturnDto.ProductInfo result = RequestReturnDto.ProductInfo.of(orderDetail, numberOfReturns);

        // then
        assertThat(result.getQuantity()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("빵빵이");
        assertThat(result.getPrice()).isEqualTo(1000L);
        assertThat(result.getImage()).isEqualTo("빵빵이 이미지");
    }

    @Test
    @DisplayName("주문 상세 리스트와 맴버십이 주어지면 ReturnRefundInfo를 반환한다.(BASIC)")
    public void returnRefundInfo_of_BASIC() {
        // given
        String memberShip = MembershipType.BASIC.getText();

        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(0L)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfReturns = 2L;

        // when
        RequestReturnDto.ReturnRefundInfo result = RequestReturnDto.ReturnRefundInfo.of(orderDetail, numberOfReturns, memberShip);

        // then
        assertThat(result)
                .extracting("deliveryFee", "returnFee", "productTotalPrice")
                .contains(BASIC.getDeliveryFee(), BASIC.getReturnFee(), 2000L);
    }

    @Test
    @DisplayName("주문 상세 리스트와 맴버십이 주어지면 ReturnRefundInfo를 반환한다.(WOW)")
    public void returnRefundInfo_of_WOW() {
        // given
        String memberShip = MembershipType.WOW.getText();

        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(0L)
                .price(1000L)
                .quantity(3L)
                .build();

        Long numberOfReturns = 2L;

        // when
        RequestReturnDto.ReturnRefundInfo result = RequestReturnDto.ReturnRefundInfo.of(orderDetail, numberOfReturns, memberShip);

        // then
        assertThat(result)
                .extracting("deliveryFee", "returnFee", "productTotalPrice")
                .contains(WOW.getDeliveryFee(), WOW.getReturnFee(), 2000L);
    }

    @Test
    @DisplayName("반품 수량이 주문 상세 수량보다 많을경우 에러를 던진다.")
    public void CancelRefundInfo_of_Error() {
        // given
        String memberShip = MembershipType.WOW.getText();

        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(0L)
                .quantity(3L)
                .build();

        Long numberOfReturns = 4L;

        // when // then
        assertThatThrownBy(() -> RequestReturnDto.ReturnRefundInfo.of(orderDetail, numberOfReturns, memberShip))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("수량이 초과 되었습니다.")
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(QUANTITY_EXCEEDED);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                });
    }

}