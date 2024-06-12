package com.objects.marketbridge.domains.order.service.port;

import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.member.domain.Address;
import com.objects.marketbridge.domains.member.domain.AddressValue;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.controller.dto.select.GetOrderHttp;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.payment.domain.CardInfo;
import com.objects.marketbridge.domains.payment.domain.Payment;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@Slf4j
class OrderQueryRepositoryTest {

    @Autowired
    OrderCommandRepository orderCommandRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    OrderQueryRepository orderQueryRepository;
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    EntityManager entityManager;

    @DisplayName("전체 주문 목록을 조회 할 경우 현재 사용자의 전체 주문 정보를 알 수 있다.")
    @Test
//    @Rollback(value = false)
    void findByMemberIdWithMemberAddress_paging_filter(){

        //given
        Member member = createMember("1");
        Address address = createAddress("서울", "세종대로", "민들레아파트");
        member.addAddress(address);
        memberRepository.save(member);

        Product product1 = createProduct(1000L, "1");
        Product product2 = createProduct(2000L, "2");
        Product product3 = createProduct(3000L, "3");
        Product product4 = createProduct(4000L, "4");
        productRepository.saveAll(List.of(product1, product2, product3, product4));

        OrderDetail orderDetail1 = createOrderDetail(product1,  1L, "1");
        OrderDetail orderDetail2 = createOrderDetail(product2,  1L, "1");
        OrderDetail orderDetail3 = createOrderDetail(product3,  1L, "1");

        OrderDetail orderDetail4 = createOrderDetail(product1,  2L, "2");
        OrderDetail orderDetail5 = createOrderDetail(product2,  2L, "2");
        OrderDetail orderDetail6 = createOrderDetail(product4,  2L, "2");

        OrderDetail orderDetail7 = createOrderDetail(product1,  3L, "3");
        OrderDetail orderDetail8 = createOrderDetail(product3,  3L, "3");
        OrderDetail orderDetail9 = createOrderDetail(product4,  3L, "3");

        OrderDetail orderDetail10 = createOrderDetail(product2, 4L, "4");
        OrderDetail orderDetail11 = createOrderDetail(product3, 4L, "4");
        OrderDetail orderDetail12 = createOrderDetail(product4, 4L, "4");

        Payment payment1 = createPayment("카드", "카카오뱅크");
        Payment payment2 = createPayment("카드", "카카오뱅크");
        Payment payment3 = createPayment("카드", "카카오뱅크");
        Payment payment4 = createPayment("카드", "카카오뱅크");


        Order order1 = createOrder(member, address, "1", List.of(orderDetail1, orderDetail2, orderDetail3), payment1); // 1,2,3
        Order order2 = createOrder(member, address, "2", List.of(orderDetail4, orderDetail5, orderDetail6), payment2); // 1,2,4
        Order order3 = createOrder(member, address, "3", List.of(orderDetail7, orderDetail8, orderDetail9), payment3); // 1,3,4
        Order order4 = createOrder(member, address, "4", List.of(orderDetail10, orderDetail11, orderDetail12), payment4); // 2,3,4

        orderCommandRepository.saveAll(List.of(order1, order2, order3, order4));

        Pageable pageSize1_3 = PageRequest.of(1, 3);
//        Pageable pageSize1_3 = PageRequest.of(0, 4);

        GetOrderHttp.Condition condition1
                = createCondition(member.getId(), "상품", String.valueOf(LocalDateTime.now().getYear()));

        //when
        Page<Order> orders1_3_c1 = orderQueryRepository.findAllPaged(condition1, pageSize1_3);

        //then
        assertThat(orders1_3_c1.getSize()).isEqualTo(3);
        assertThat(orders1_3_c1.getContent().size()).isEqualTo(1);
        assertThat(orders1_3_c1.getTotalElements()).isEqualTo(4);
        assertThat(orders1_3_c1.getNumberOfElements()).isEqualTo(1);
        assertThat(orders1_3_c1.getTotalPages()).isEqualTo(2);

    }

    private GetOrderHttp.Condition createCondition(Long memberId, String keyword, String year) {
        return GetOrderHttp.Condition.builder()
                .memberId(memberId)
                .keyword(keyword)
                .year(year)
                .build();
    }

    @DisplayName("상세 주문 조회 하기(member, address, orderDetails, product, payment 전부 fetch join)")
    @Test
    void getOrderDetails() {
        // given
        Member member = createMember("1");
        Address address = createAddress("서울", "세종대로", "민들레아파트");
        member.addAddress(address);
        memberRepository.save(member);

        Product product1 = createProduct(1000L, "1");
        Product product2 = createProduct(2000L, "2");
        Product product3 = createProduct(3000L, "3");
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderDetail orderDetail1 = createOrderDetail(product1,  1L, "1");
        OrderDetail orderDetail2 = createOrderDetail(product2,  1L, "1");
        OrderDetail orderDetail3 = createOrderDetail(product3,  1L, "1");

        Payment payment = createPayment("카드", "카카오뱅크");

        Order order = createOrder(member, address, "1", List.of(orderDetail1, orderDetail2, orderDetail3), payment);
        orderCommandRepository.save(order);

        // when
        Order findOrder = orderQueryRepository.findByOrderIdFetchJoin(order.getId());

        //then
        assertThat(findOrder.getMember().getId()).isEqualTo(member.getId());
        assertThat(findOrder.getAddress().getAddressValue()).isEqualTo(address.getAddressValue());

        assertThat(findOrder.getOrderDetails()).hasSize(3);
        assertThat(findOrder.getOrderDetails().get(0).getId()).isEqualTo(orderDetail1.getId());
        assertThat(findOrder.getOrderDetails().get(1).getId()).isEqualTo(orderDetail2.getId());
        assertThat(findOrder.getOrderDetails().get(2).getId()).isEqualTo(orderDetail3.getId());

        assertThat(findOrder.getOrderDetails().get(0).getProduct().getId()).isEqualTo(product1.getId());
        assertThat(findOrder.getOrderDetails().get(1).getProduct().getId()).isEqualTo(product2.getId());
        assertThat(findOrder.getOrderDetails().get(2).getProduct().getId()).isEqualTo(product3.getId());

        assertThat(findOrder.getPayment().getPaymentMethod()).isEqualTo(payment.getPaymentMethod());
        assertThat(findOrder.getPayment().getCardInfo().getCardIssuerName()).isEqualTo(payment.getCardInfo().getCardIssuerName());

    }

    private Order createOrder(Member member1, Address address, String orderNo, List<OrderDetail> orderDetails, Payment payment) {

        Order order = Order.builder()
                .member(member1)
                .address(address)
                .orderNo(orderNo)
                .build();

        // order <-> orderDetail 연관관계
        orderDetails.forEach(order::addOrderDetail);

        // payment <-> payment 연관관계
        if (payment != null) {
            order.linkPayment(payment);
        }

        return order;
    }

    private OrderDetail createOrderDetail(Product product,  Long quantity, String orderNo) {
        return OrderDetail.builder()
                .product(product)
                .quantity(quantity)
                .reducedQuantity(0L)
                .price(product.getPrice() * quantity)
                .orderNo(orderNo)
                .build();
    }

    private Product createProduct(Long price, String no) {
        return Product.builder()
                .price(price)
                .thumbImg("썸네일"+no)
                .name("상품"+no)
                .build();
    }

    private Member createMember(String no) {
        return Member.builder()
                .name("홍길동"+no)
                .build();
    }

    private Address createAddress(String city, String street, String detail) {
        return Address.builder().addressValue(AddressValue.builder()
                        .city(city)
                        .street(street)
                        .detail(detail).build())
                .build();
    }

    private Payment createPayment(String paymentMethod, String cardIssuerName) {
        CardInfo cardInfo = CardInfo.builder()
                .cardIssuerName(cardIssuerName)
                .build();

        return Payment.builder()
                .paymentMethod(paymentMethod)
                .cardInfo(cardInfo)
                .build();
    }
}