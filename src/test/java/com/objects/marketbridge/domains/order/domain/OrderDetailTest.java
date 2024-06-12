package com.objects.marketbridge.domains.order.domain;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.order.mock.TestDateTimeHolder;
import com.objects.marketbridge.domains.product.domain.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.*;
import static com.objects.marketbridge.domains.order.domain.StatusCodeType.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class OrderDetailTest {

    @Test
    @DisplayName("memberCoupon이 존재하면 memberCoupon의 상태를 바꾼다.")
    public void changeMemberCouponInfo_isNotNull() {
        // given
        MemberCoupon memberCoupon = MemberCoupon.builder()
                .isUsed(false)
                .usedDate(LocalDateTime.now())
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(memberCoupon)
                .build();

        // when
        orderDetail.changeMemberCouponInfo(null);

        // then
        assertThat(orderDetail.getMemberCoupon().getIsUsed()).isTrue();
        assertThat(orderDetail.getMemberCoupon().getUsedDate()).isNull();
    }

    @Test
    @DisplayName("memberCoupon이 없다면 memberCoupon의 상태를 바꾸지 않는다.")
    public void changeMemberCouponInfo_isNull() {
        // given
        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(null)
                .build();

        // when
        orderDetail.changeMemberCouponInfo(null);

        // then
        assertThat(orderDetail.getMemberCoupon()).isNull();
    }

    @Test
    @DisplayName("memberCoupon이 존재하면 memberCoupon의 상태를 바꾼다.")
    public void returnMemberCoupon_isNotNull() {
        // given
        MemberCoupon memberCoupon = MemberCoupon.builder()
                .isUsed(false)
                .usedDate(LocalDateTime.now())
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(memberCoupon)
                .build();

        // when
        orderDetail.returnMemberCoupon();

        // then
        assertThat(orderDetail.getMemberCoupon().getIsUsed()).isTrue();
        assertThat(orderDetail.getMemberCoupon().getUsedDate()).isNull();
    }

    @Test
    @DisplayName("memberCoupon이 없다면 memberCoupon의 상태를 바꾸지 않는다.")
    public void returnMemberCoupon_isNull() {
        // given
        OrderDetail orderDetail = OrderDetail.builder()
                .memberCoupon(null)
                .build();

        // when
        orderDetail.returnMemberCoupon();

        // then
        assertThat(orderDetail.getMemberCoupon()).isNull();
    }

    @Test
    @DisplayName("취소된 상품의 총 금액을 반환한다.")
    public void cancelAmount() {
        // given
        OrderDetail orderDetail = OrderDetail.builder()
                .reducedQuantity(2L)
                .price(1000L)
                .build();

        // when
        Integer cancelAmount = orderDetail.cancelAmount();

        // then
        assertThat(cancelAmount).isEqualTo(2000L);
    }

    @Test
    @DisplayName("배송 완료된 상품은 취소할 수 없다.")
    public void cancel_DELIVERY_COMPLETED_ERROR() {
        // given
        OrderDetail orderDetail = OrderDetail.builder()
                .statusCode(DELIVERY_COMPLETED.getCode())
                .build();

        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 11, 27);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        // when // then
        assertThatThrownBy(() -> orderDetail.cancel(null,  dateTimeHolder))
                .hasMessage(NON_CANCELLABLE_PRODUCT.getMessage())
                .isInstanceOf(CustomLogicException.class)
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(NON_CANCELLABLE_PRODUCT);
                    assertThat(customLogicException.getTimestamp()).isEqualTo(now);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                });
    }

    @Test
    @DisplayName("부분취소된 상품은 취소할 수 없다.")
    public void cancel_ORDER_PARTIAL_CANCEL_ERROR() {
        // given
        OrderDetail orderDetail = OrderDetail.builder()
                .statusCode(ORDER_PARTIAL_CANCEL.getCode())
                .build();

        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 11, 27);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        // when // then
        assertThatThrownBy(() -> orderDetail.cancel(null, dateTimeHolder))
                .hasMessage(NON_CANCELLABLE_PRODUCT.getMessage())
                .isInstanceOf(CustomLogicException.class)
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(NON_CANCELLABLE_PRODUCT);
                    assertThat(customLogicException.getTimestamp()).isEqualTo(now);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                });
    }

    @Test
    @DisplayName("전체 수량보다 취소 수량이 많을 경우 에러가 발생한다.")
    public void cancel_QUANTITY_EXCEEDED_ERROR() {
        // given
        OrderDetail orderDetail = OrderDetail.builder()
                .quantity(5L)
                .reducedQuantity(4L)
                .statusCode(ORDER_RECEIVED.getCode())
                .build();

        Long numberOfCancellations = 2L;
        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 11, 27);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        // when // then
        assertThatThrownBy(() -> orderDetail.cancel(numberOfCancellations, dateTimeHolder))
                .hasMessage(QUANTITY_EXCEEDED.getMessage())
                .isInstanceOf(CustomLogicException.class)
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(QUANTITY_EXCEEDED);
                    assertThat(customLogicException.getTimestamp()).isEqualTo(now);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                });
    }

    @Test
    @DisplayName("부분 취소의 경우 상품재고, 취소수량, 쿠폰상태가 변경된다.")
    public void cancel_partial() {
        // given
        Product product = Product.builder()
                .stock(5L)
                .build();

        LocalDateTime usedDate = LocalDateTime.of(2024, 2, 9, 11, 30);
        MemberCoupon memberCoupon = MemberCoupon.builder()
                .isUsed(true)
                .usedDate(usedDate)
                .build();

        String statusCode = PAYMENT_COMPLETED.getCode();
        OrderDetail orderDetail = OrderDetail.builder()
                .quantity(5L)
                .product(product)
                .memberCoupon(memberCoupon)
                .reducedQuantity(0L)
                .statusCode(statusCode)
                .build();

        Long numberOfCancellations = 2L;

        // when
        orderDetail.cancel(numberOfCancellations, null);

        // then
        assertThat(product.getStock()).isEqualTo(7L);
        assertThat(memberCoupon.getUsedDate()).isNull();
        assertThat(memberCoupon.getIsUsed()).isFalse();
        assertThat(orderDetail.getQuantity()).isEqualTo(5L);
        assertThat(orderDetail.getReducedQuantity()).isEqualTo(numberOfCancellations);
        assertThat(orderDetail.getStatusCode()).isEqualTo(ORDER_CANCEL.getCode());
    }

    @Test
    @DisplayName("모든 취소의 경우 상품재고, 취소수량, 쿠폰상태, 이유, 주문상태가 변경된다.")
    public void cancel() {
        // given
        Product product = Product.builder()
                .stock(5L)
                .build();

        LocalDateTime usedDate = LocalDateTime.of(2024, 2, 9, 11, 30);
        MemberCoupon memberCoupon = MemberCoupon.builder()
                .isUsed(true)
                .usedDate(usedDate)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .quantity(5L)
                .product(product)
                .memberCoupon(memberCoupon)
                .reducedQuantity(0L)
                .statusCode(PAYMENT_COMPLETED.getCode())
                .build();

        Long numberOfCancellations = 5L;
        String reason = "단순변심";

        // when
        orderDetail.cancel(numberOfCancellations, null);

        // then
        assertThat(product.getStock()).isEqualTo(10L);
        assertThat(memberCoupon.getUsedDate()).isNull();
        assertThat(memberCoupon.getIsUsed()).isFalse();
        assertThat(orderDetail.getQuantity()).isEqualTo(5L);
        assertThat(orderDetail.getReducedQuantity()).isEqualTo(numberOfCancellations);
        assertThat(orderDetail.getStatusCode()).isEqualTo(ORDER_CANCEL.getCode());
    }

    @Test
    @DisplayName("배송완료된 상태를 제외하면 에러를 발생시킨다.")
    public void returns_NOT_DELIVERY_COMPLETED_ERROR() {
        // given
        OrderDetail orderDetail = OrderDetail.builder()
                .statusCode(ORDER_RECEIVED.getCode())
                .build();
        LocalDateTime now = LocalDateTime.of(2024, 2, 10, 5, 3);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        // when // then
        assertThatThrownBy(() -> orderDetail.returns( null, dateTimeHolder))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage(NON_RETURNABLE_PRODUCT.getMessage())
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                    assertThat(customLogicException.getErrorCode()).isEqualTo(NON_RETURNABLE_PRODUCT);
                    assertThat(customLogicException.getTimestamp()).isEqualTo(now);
                });
    }

    @Test
    @DisplayName("반품 수량을 초과하면 에러를 발생시킨다.")
    public void returns_QUANTITY_EXCEEDED_ERROR() {
        // given
        OrderDetail orderDetail = OrderDetail.builder()
                .statusCode(DELIVERY_COMPLETED.getCode())
                .quantity(5L)
                .reducedQuantity(0L)
                .build();
        LocalDateTime now = LocalDateTime.of(2024, 2, 10, 5, 3);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        Long numberOfReturns = 6L;

        // when // then
        assertThatThrownBy(() -> orderDetail.returns( numberOfReturns, dateTimeHolder))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("수량이 초과 되었습니다.")
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                    assertThat(customLogicException.getErrorCode()).isEqualTo(QUANTITY_EXCEEDED);
                    assertThat(customLogicException.getTimestamp()).isEqualTo(now);
                });
    }

    @Test
    @DisplayName("부분 반품하면 상품재고, 수량감소, 쿠폰 반환만 한다.")
    public void returns_partial() {
        // given
        Product product = Product.builder()
                .stock(5L)
                .build();

        LocalDateTime usedDate = LocalDateTime.of(2024, 2, 9, 11, 30);
        MemberCoupon memberCoupon = MemberCoupon.builder()
                .isUsed(true)
                .usedDate(usedDate)
                .build();

        String statusCode = DELIVERY_COMPLETED.getCode();
        OrderDetail orderDetail = OrderDetail.builder()
                .quantity(5L)
                .product(product)
                .memberCoupon(memberCoupon)
                .reducedQuantity(0L)
                .statusCode(statusCode)
                .build();

        LocalDateTime now = LocalDateTime.of(2024, 2, 10, 5, 15);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        Long numberOfReturns = 2L;

        // when
        orderDetail.returns(numberOfReturns, dateTimeHolder);

        // then
        assertThat(product.getStock()).isEqualTo(7L);
        assertThat(memberCoupon.getUsedDate()).isNull();
        assertThat(memberCoupon.getIsUsed()).isFalse();
        assertThat(orderDetail.getQuantity()).isEqualTo(5L);
        assertThat(orderDetail.getReducedQuantity()).isEqualTo(numberOfReturns);
        assertThat(orderDetail.getStatusCode()).isEqualTo(RETURN_INIT.getCode());
    }

    @Test
    @DisplayName("모두 반품하면 상품재고, 수량감소, 쿠폰 반환, 상태변화, 이유를 변경한다.")
    public void returns() {
        // given
        Product product = Product.builder()
                .stock(5L)
                .build();

        LocalDateTime usedDate = LocalDateTime.of(2024, 2, 9, 11, 30);
        MemberCoupon memberCoupon = MemberCoupon.builder()
                .isUsed(true)
                .usedDate(usedDate)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .quantity(5L)
                .product(product)
                .memberCoupon(memberCoupon)
                .reducedQuantity(0L)
                .statusCode(DELIVERY_COMPLETED.getCode())
                .build();

        LocalDateTime now = LocalDateTime.of(2024, 2, 10, 5, 16);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(now)
                .build();

        Long numberOfReturns = 5L;

        // when
        orderDetail.returns(numberOfReturns, dateTimeHolder);

        // then
        assertThat(product.getStock()).isEqualTo(10L);
        assertThat(memberCoupon.getUsedDate()).isNull();
        assertThat(memberCoupon.getIsUsed()).isFalse();
        assertThat(orderDetail.getQuantity()).isEqualTo(5L);
        assertThat(orderDetail.getReducedQuantity()).isEqualTo(numberOfReturns);
        assertThat(orderDetail.getStatusCode()).isEqualTo(RETURN_INIT.getCode());
    }

    @Test
    @DisplayName("주어진 수량만큼 합계를 반환한다.")
    public void totalAmount() {
        // given
        Long numberOfCancellations = 3L;
        OrderDetail orderDetail = createOrderDetail(10L, 1000L);

        // when
        Integer result = orderDetail.totalAmount(numberOfCancellations, LocalDateTime.now());

        // then
        assertThat(result).isEqualTo(3000L);
    }

    @Test
    @DisplayName("취소한 수량이 기존 수량보다 많다면 에러를 생성한다.")
    public void totalAmount_with_error() {
        // given
        Long numberOfCancellations = 11L;
        OrderDetail orderDetail = createOrderDetail(10L, 1000L);

        LocalDateTime now = LocalDateTime.of(2024, 2, 9, 11, 27);
        // when // then
        assertThatThrownBy(() -> orderDetail.totalAmount(numberOfCancellations, now))
                .hasMessage("수량이 초과 되었습니다.")
                .isInstanceOf(CustomLogicException.class)
                .satisfies(exception -> {
                    CustomLogicException customLogicException = (CustomLogicException) exception;
                    assertThat(customLogicException.getErrorCode()).isEqualTo(QUANTITY_EXCEEDED);
                    assertThat(customLogicException.getTimestamp()).isEqualTo(now);
                    assertThat(customLogicException.getHttpStatus()).isEqualTo(BAD_REQUEST);
                });
    }

    @Test
    @DisplayName("수량이 주어지지 않으면 모든 수량에 대한 합계를 반환한다.")
    public void totalAmount_All() {
        // given
        OrderDetail orderDetail = createOrderDetail(10L, 1000L);

        // when
        Integer result = orderDetail.totalAmount();

        // then
        assertThat(result).isEqualTo(10000L);
    }

    @DisplayName("이미 사용한 쿠폰 혹은 유효기간이 만료된 쿠폰은 사용할 수 없다")
    @Test
    void create_exception_1(){
        //given
        Member member = Member.builder().email("test@email.com").build();
        Coupon coupon1 = Coupon.builder().name("1000원 할인").productGroupId(111111L).minimumPrice(5000L).endDate(LocalDateTime.of(2024, 1, 1, 0, 0, 0)).build();
        Coupon coupon2 = Coupon.builder().name("1000원 할인").productGroupId(222222L).minimumPrice(5000L).endDate(LocalDateTime.of(9999, 1, 1, 0, 0, 0)).build();
        MemberCoupon memberCoupon1 = MemberCoupon.builder().member(member).coupon(coupon1).isUsed(false).endDate(LocalDateTime.of(2024, 1, 1, 0, 0, 0)).build();
        MemberCoupon memberCoupon2 = MemberCoupon.builder().member(member).coupon(coupon2).isUsed(true).endDate(LocalDateTime.of(9999, 1, 1, 0, 0, 0)).build();
        Product product1 = Product.builder().productNo("111111 - 111111").build();
        Product product2 = Product.builder().productNo("222222 - 222222").build();
        DateTimeHolder dateTimeHolder = new TestDateTimeHolder(LocalDateTime.of(3024, 1, 1, 0, 0, 0), null, null, null);

        Throwable thrown1 = catchThrowable(() -> OrderDetail.create(null, null, product1, null, memberCoupon1, 10000L, 1L, null,  dateTimeHolder));
        Throwable thrown2 = catchThrowable(() -> OrderDetail.create(null, null, product2, null, memberCoupon2, 10000L, 1L, null,  dateTimeHolder));

        //then
        Assertions.assertThat(thrown1).isInstanceOf(CustomLogicException.class);
        Assertions.assertThat(thrown2).isInstanceOf(CustomLogicException.class);
    }

    @DisplayName("최소주문금액을 넘지 못하면 쿠폰을 사용 할 수 없다")
    @Test
    void create_exception_2(){
        //given
        Member member = Member.builder().email("test@email.com").build();
        Coupon coupon1 = Coupon.builder().productGroupId(111111L).name("1000원 할인").minimumPrice(15000L).endDate(LocalDateTime.of(2024, 1, 1, 0, 0, 0)).build();
        MemberCoupon memberCoupon1 = MemberCoupon.builder().member(member).coupon(coupon1).isUsed(false).endDate(LocalDateTime.of(2024, 1, 1, 0, 0, 0)).build();
        Product product1 = Product.builder().productNo("111111 - 111111").price(2000L).build();
        DateTimeHolder dateTimeHolder = new TestDateTimeHolder(LocalDateTime.of(3024, 1, 1, 0, 0, 0), null, null, null);

        Throwable thrown1 = catchThrowable(() -> OrderDetail.create(null, null, product1, null, memberCoupon1, 14000L, 1L, null,  dateTimeHolder));

        //then
        Assertions.assertThat(thrown1).isInstanceOf(CustomLogicException.class);
    }

    private static OrderDetail createOrderDetail(long quantity, long price) {
        return OrderDetail.builder()
                .quantity(quantity)
                .price(price)
                .reducedQuantity(0L)
                .build();
    }
}