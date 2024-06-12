package com.objects.marketbridge.domains.order.service;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.mock.BaseFakeCartRepository;
import com.objects.marketbridge.domains.cart.mock.FakeCartCommandRepository;
import com.objects.marketbridge.domains.cart.mock.FakeCartQueryRepository;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.mock.FakeCouponRepository;
import com.objects.marketbridge.domains.coupon.mock.FakeMemberCouponRepository;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.coupon.service.port.MemberCouponRepository;
import com.objects.marketbridge.domains.member.domain.Address;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.mock.FakeMemberRepository;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.mock.*;
import com.objects.marketbridge.domains.order.service.dto.CreateOrderDto;
import com.objects.marketbridge.domains.order.service.port.*;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.mock.FakeProductRepository;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Slf4j
public class CreateOrderServiceTestWithFake {

    OrderQueryRepository orderQueryRepository = new FakeOrderQueryRepository();
    CouponRepository couponRepository = new FakeCouponRepository();
    CartQueryRepository cartQueryRepository = new FakeCartQueryRepository();
    CartCommandRepository cartCommandRepository = new FakeCartCommandRepository();

    OrderDetailCommandRepository orderDetailCommandRepository = new FakeOrderDetailCommandRepository();
    OrderCommandRepository orderCommandRepository = new FakeOrderCommandRepository();
    ProductRepository productRepository = new FakeProductRepository();
    MemberRepository memberRepository = new FakeMemberRepository();
    MemberCouponRepository memberCouponRepository = new FakeMemberCouponRepository();
    AddressRepository addressRepository = new FakeAddressRepository();
    DateTimeHolder dateTimeHolder = TestDateTimeHolder
            .builder()
            .createTime(LocalDateTime.of(2024, 4, 19, 12, 0, 0))
            .now(LocalDateTime.of(2024, 4, 19, 12, 0, 0))
            .build();

    CreateOrderService createOrderService = CreateOrderService.builder()
            .cartCommandRepository(cartCommandRepository)
            .orderDetailCommandRepository(orderDetailCommandRepository)
            .orderCommandRepository(orderCommandRepository)
            .productRepository(productRepository)
            .memberRepository(memberRepository)
            .memberCouponRepository(memberCouponRepository)
            .addressRepository(addressRepository)
            .dateTimeHolder(dateTimeHolder)
            .build();

    @AfterEach
    void clear() {
        BaseFakeOrderRepository.getInstance().clear();
        BaseFakeOrderDetailRepository.getInstance().clear();
        BaseFakeCartRepository.getInstance().clear();
        couponRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        memberCouponRepository.deleteAllInBatch();
        addressRepository.deleteAllInBatch();
    }
    @DisplayName("주문을 생성 할 수 있다")
    @Test
    void create(){
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());
        Coupon coupon = couponRepository.save(Coupon.builder()
                .productGroupId(111111L)
                .price(1000L)
                .minimumPrice(10000L)
                .startDate(LocalDateTime.of(2024,1,1,12,0,0))
                .endDate(LocalDateTime.of(2025,1,1,12,0,0))
                .build());

        MemberCoupon memberCoupon =
                memberCouponRepository.save(MemberCoupon.builder()
                        .member(member)
                        .isUsed(false)
                        .endDate(coupon.getEndDate())
                        .build());
        coupon.addMemberCoupon(memberCoupon);

        Address address = addressRepository.save(Address.builder().build());
        member.addAddress(address);
        Product product = Product.builder()
                .stock(999L)
                .productNo("111111 - 111111")
                .name("가방")
                .price(20000L)
                .build();

        productRepository.save(product);

        cartCommandRepository.save(Cart.builder().member(member).product(product).build());

        CreateOrderDto.ProductDto productDto = CreateOrderDto.ProductDto.builder()
                .productId(1L)
                .price(20000L)
                .quantity(1L)
                .hasCouponUsed(true)
                .couponId(1L)
                .couponPrice(1000L)
                .couponMinimumPrice(10000L)
                .couponEndDate("2024-01-01 12:00:00")
                .couponEndDate("2024-01-01 12:00:00")
                .build();

        CreateOrderDto createOrderDto = CreateOrderDto.builder()
                .tid("tid")
                .memberId(1L)
                .addressId(1L)
                .orderName("가방")
                .orderNo("1111-2222-3333-4444")
                .totalOrderPrice(20000L)
                .realOrderPrice(19000L)
                .totalDiscountPrice(1000L)
                .productValues(List.of(productDto))
                .build();

        //when
        createOrderService.create(createOrderDto);
        Order order = orderQueryRepository.findByOrderNo("1111-2222-3333-4444");

        //then
        assertThat(order).hasFieldOrPropertyWithValue("member", member);
        assertThat(order).hasFieldOrPropertyWithValue("address", address);
        assertThat(order).hasFieldOrPropertyWithValue("orderName", "가방");
        assertThat(order).hasFieldOrPropertyWithValue("orderNo", "1111-2222-3333-4444");
        assertThat(order).hasFieldOrPropertyWithValue("totalPrice", 20000L);
        assertThat(order).hasFieldOrPropertyWithValue("realPrice", 19000L);
        assertThat(order).hasFieldOrPropertyWithValue("totalDiscount", 1000L);
        assertThat(order).hasFieldOrPropertyWithValue("tid", "tid");

    }

    @DisplayName("주문 상세가 생성 된다")
    @Test
    void create_order_detail(){
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());
        Coupon coupon = couponRepository.save(Coupon.builder()
                .productGroupId(111111L)
                .price(1000L)
                .minimumPrice(10000L)
                .startDate(LocalDateTime.of(2024,1,1,12,0,0))
                .endDate(LocalDateTime.of(2025,1,1,12,0,0))
                .build());

        MemberCoupon memberCoupon =
                memberCouponRepository.save(MemberCoupon.builder()
                        .member(member)
                        .isUsed(false)
                        .endDate(coupon.getEndDate())
                        .build());
        coupon.addMemberCoupon(memberCoupon);

        Address address = addressRepository.save(Address.builder().build());
        member.addAddress(address);
        Product product = Product.builder()
                .stock(999L)
                .productNo("111111 - 111111")
                .name("가방")
                .price(20000L)
                .build();

        productRepository.save(product);
        cartCommandRepository.save(Cart.builder().member(member).product(product).build());


        CreateOrderDto.ProductDto productDto = CreateOrderDto.ProductDto.builder()
                .productId(1L)
                .price(20000L)
                .quantity(1L)
                .hasCouponUsed(true)
                .couponId(1L)
                .couponPrice(1000L)
                .couponMinimumPrice(10000L)
                .couponEndDate("2024-01-01 12:00:00")
                .couponEndDate("2024-01-01 12:00:00")
                .build();

        CreateOrderDto createOrderDto = CreateOrderDto.builder()
                .tid("tid")
                .memberId(1L)
                .addressId(1L)
                .orderName("가방")
                .orderNo("1111-2222-3333-4444")
                .totalOrderPrice(20000L)
                .realOrderPrice(19000L)
                .totalDiscountPrice(1000L)
                .productValues(List.of(productDto))
                .build();

        //when
        createOrderService.create(createOrderDto);
        List<OrderDetail> orderDetails = orderQueryRepository.findByOrderNo("1111-2222-3333-4444").getOrderDetails();

        //then
        assertThat(orderDetails).hasSize(1);
//        log.info("주문상세 아이디 : {}",orderDetails.get(0).getId());
        assertThat(orderDetails)
                .extracting(
                        OrderDetail::getOrderNo,
                        OrderDetail::getId,
                        OrderDetail::getProduct,
                        OrderDetail::getPrice,
                        OrderDetail::getMemberCoupon,
                        OrderDetail::getQuantity)
                .contains(
                        Tuple.tuple(
                                "1111-2222-3333-4444",
                                1L,
                                product,
                                20000L,
                                memberCoupon,
                                1L)
                );
    }

    @DisplayName("쿠폰을 사용한 경우 쿠폰 사용정보가 변경되어야한다")
    @Test
    void create_change_coupon_usage(){
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());
        Coupon coupon = couponRepository.save(Coupon.builder()
                .productGroupId(111111L)
                .price(1000L)
                .minimumPrice(10000L)
                .startDate(LocalDateTime.of(2024,1,1,12,0,0))
                .endDate(LocalDateTime.of(2025,1,1,12,0,0))
                .build());

        MemberCoupon memberCoupon =
                memberCouponRepository.save(MemberCoupon.builder()
                        .member(member)
                        .isUsed(false)
                        .endDate(coupon.getEndDate())
                        .build());
        coupon.addMemberCoupon(memberCoupon);

        Address address = addressRepository.save(Address.builder().build());
        member.addAddress(address);
        Product product = Product.builder()
                .stock(999L)
                .productNo("111111 - 111111")
                .name("가방")
                .price(20000L)
                .build();

        productRepository.save(product);

        cartCommandRepository.save(Cart.builder().member(member).product(product).build());

        CreateOrderDto.ProductDto productDto = CreateOrderDto.ProductDto.builder()
                .productId(1L)
                .price(20000L)
                .quantity(1L)
                .hasCouponUsed(true)
                .couponId(1L)
                .couponPrice(1000L)
                .couponMinimumPrice(10000L)
                .couponEndDate("2024-01-01 12:00:00")
                .couponEndDate("2024-01-01 12:00:00")
                .build();

        CreateOrderDto createOrderDto = CreateOrderDto.builder()
                .tid("tid")
                .memberId(1L)
                .addressId(1L)
                .orderName("가방")
                .orderNo("1111-2222-3333-4444")
                .totalOrderPrice(20000L)
                .realOrderPrice(19000L)
                .totalDiscountPrice(1000L)
                .productValues(List.of(productDto))
                .build();

        //when
        createOrderService.create(createOrderDto);
        MemberCoupon findMemberCoupon = memberCouponRepository.findById(1L);

        //then
        assertThat(findMemberCoupon.getIsUsed()).isTrue();
        assertThat(findMemberCoupon.getUsedDate()).isEqualTo(dateTimeHolder.getTimeNow());
    }

    @DisplayName("주문 생성시 재고가 감소된다")
    @Test
    void create_stock_decrease(){
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());
        Coupon coupon = couponRepository.save(Coupon.builder()
                .productGroupId(111111L)
                .price(1000L)
                .minimumPrice(10000L)
                .startDate(LocalDateTime.of(2024,1,1,12,0,0))
                .endDate(LocalDateTime.of(2025,1,1,12,0,0))
                .build());

        MemberCoupon memberCoupon =
                memberCouponRepository.save(MemberCoupon.builder()
                        .member(member)
                        .isUsed(false)
                        .endDate(coupon.getEndDate())
                        .build());
        coupon.addMemberCoupon(memberCoupon);

        Address address = addressRepository.save(Address.builder().build());
        member.addAddress(address);
        Product product = Product.builder()
                .stock(999L)
                .productNo("111111 - 111111")
                .name("가방")
                .price(20000L)
                .build();

        productRepository.save(product);

        cartCommandRepository.save(Cart.builder().member(member).product(product).build());

        CreateOrderDto.ProductDto productDto = CreateOrderDto.ProductDto.builder()
                .productId(1L)
                .price(20000L)
                .quantity(1L)
                .hasCouponUsed(true)
                .couponId(1L)
                .couponPrice(1000L)
                .couponMinimumPrice(10000L)
                .couponEndDate("2024-01-01 12:00:00")
                .couponEndDate("2024-01-01 12:00:00")
                .build();

        CreateOrderDto createOrderDto = CreateOrderDto.builder()
                .tid("tid")
                .memberId(1L)
                .addressId(1L)
                .orderName("가방")
                .orderNo("1111-2222-3333-4444")
                .totalOrderPrice(20000L)
                .realOrderPrice(19000L)
                .totalDiscountPrice(1000L)
                .productValues(List.of(productDto))
                .build();

        //when
        createOrderService.create(createOrderDto);
        Product findProduct = productRepository.findById(1L);

        //then
        assertThat(findProduct.getStock()).isEqualTo(998L);
    }

    @DisplayName("주문 완료시 장바구니 삭제")
    @Test
    void create_remove_cart_item() {
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());
        Address address = addressRepository.save(Address.builder().build());
        member.addAddress(address);

        Product product1 = productRepository.save(Product.builder()
                .stock(999L)
                .productNo("111111 - 111111")
                .name("가방")
                .price(20000L)
                .build());

        Product product2 = productRepository.save(Product.builder()
                .stock(999L)
                .productNo("111111 - 111111")
                .name("책")
                .price(20000L)
                .build());

        Product product3 = productRepository.save(Product.builder()
                .stock(999L)
                .productNo("111111 - 111111")
                .name("신발")
                .price(20000L)
                .build());

        cartCommandRepository.save(Cart.builder().member(member).product(product1).build());
        cartCommandRepository.save(Cart.builder().member(member).product(product2).build());
        cartCommandRepository.save(Cart.builder().member(member).product(product3).build());

        CreateOrderDto.ProductDto productDto1 = CreateOrderDto.ProductDto.builder()
                .productId(1L)
                .price(20000L)
                .quantity(1L)
                .hasCouponUsed(false)
                .build();
        CreateOrderDto.ProductDto productDto2 = CreateOrderDto.ProductDto.builder()
                .productId(2L)
                .price(20000L)
                .quantity(1L)
                .hasCouponUsed(false)
                .build();
        CreateOrderDto.ProductDto productDto3 = CreateOrderDto.ProductDto.builder()
                .productId(3L)
                .price(20000L)
                .quantity(1L)
                .hasCouponUsed(false)
                .build();

        CreateOrderDto createOrderDto = CreateOrderDto.builder()
                .tid("tid")
                .memberId(1L)
                .addressId(1L)
                .orderName("가방")
                .orderNo("1111-2222-3333-4444")
                .totalOrderPrice(60000L)
                .realOrderPrice(60000L)
                .totalDiscountPrice(0L)
                .productValues(List.of(productDto1, productDto2, productDto3))
                .build();

        //when
        createOrderService.create(createOrderDto);

        //then
        List<Cart> carts = cartQueryRepository.findAll();
        assertThat(carts).isEmpty();
    }

    @DisplayName("주문량이 재고보다 많을 경우 예외를 발생시켜야한다")
    @Test
    void create_error_stock(){
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());
        Coupon coupon = couponRepository.save(Coupon.builder()
                .productGroupId(111111L)
                .price(1000L)
                .minimumPrice(10000L)
                .startDate(LocalDateTime.of(2024,1,1,12,0,0))
                .endDate(LocalDateTime.of(2025,1,1,12,0,0))
                .build());

        MemberCoupon memberCoupon =
                memberCouponRepository.save(MemberCoupon.builder()
                        .member(member)
                        .isUsed(false)
                        .endDate(coupon.getEndDate())
                        .build());
        coupon.addMemberCoupon(memberCoupon);

        Address address = addressRepository.save(Address.builder().build());
        member.addAddress(address);
        Product product = Product.builder()
                .stock(0L)
                .productNo("111111 - 111111")
                .name("가방")
                .price(20000L)
                .build();

        productRepository.save(product);

        cartCommandRepository.save(Cart.builder().member(member).product(product).build());

        CreateOrderDto.ProductDto productDto = CreateOrderDto.ProductDto.builder()
                .productId(1L)
                .price(20000L)
                .quantity(1L)
                .hasCouponUsed(true)
                .couponId(1L)
                .couponPrice(1000L)
                .couponMinimumPrice(10000L)
                .couponEndDate("2024-01-01 12:00:00")
                .couponEndDate("2024-01-01 12:00:00")
                .build();

        CreateOrderDto createOrderDto = CreateOrderDto.builder()
                .tid("tid")
                .memberId(1L)
                .addressId(1L)
                .orderName("가방")
                .orderNo("1111-2222-3333-4444")
                .totalOrderPrice(20000L)
                .realOrderPrice(19000L)
                .totalDiscountPrice(1000L)
                .productValues(List.of(productDto))
                .build();

        //when
        Throwable thrown = catchThrowable(() -> createOrderService.create(createOrderDto));

        //then
        assertThat(thrown).isInstanceOf(CustomLogicException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OUT_OF_STOCK)
                .hasMessage(ErrorCode.OUT_OF_STOCK.getMessage());
    }
    



}
