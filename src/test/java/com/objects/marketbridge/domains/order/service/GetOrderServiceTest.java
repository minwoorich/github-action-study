package com.objects.marketbridge.domains.order.service;

import com.objects.marketbridge.common.kakao.enums.CardCoType;
import com.objects.marketbridge.common.responseobj.PageResponse;
import com.objects.marketbridge.domains.member.domain.Address;
import com.objects.marketbridge.domains.member.domain.AddressValue;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.controller.dto.select.GetOrderDetailHttp;
import com.objects.marketbridge.domains.order.controller.dto.select.GetOrderHttp;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.service.port.OrderCommandRepository;
import com.objects.marketbridge.domains.order.service.port.OrderDtoRepository;
import com.objects.marketbridge.domains.order.service.port.OrderQueryRepository;
import com.objects.marketbridge.domains.payment.domain.Amount;
import com.objects.marketbridge.domains.payment.domain.CardInfo;
import com.objects.marketbridge.domains.payment.domain.Payment;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.objects.marketbridge.domains.order.domain.StatusCodeType.PAYMENT_COMPLETED;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class GetOrderServiceTest {

    @Autowired
    GetOrderService getOrderService;
    @Autowired OrderCommandRepository orderCommandRepository;
    @Autowired OrderDtoRepository orderDtoRepository;
    @Autowired OrderQueryRepository orderQueryRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired ProductRepository productRepository;


    @AfterEach
    void clear() {
        orderCommandRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }
    @BeforeEach
    void init() {
        Address address = createAddress(createAddressValue("01011112222", "홍길동", "서울", "세종대로","11111", "민들레아파트110동3222호", "우리집"),true);
        Member member = Member.create(MembershipType.BASIC.getText(), "email.com", "1234", "홍길동", "01011112222", true, true);
        member.addAddress(address);
        memberRepository.save(member);

        Product product1 = Product.create(null, "상품1", 1000L, false, 100L, "썸네일1", 0L, "1번");
        Product product2 = Product.create(null,  "상품2", 2000L, false, 100L, "썸네일2", 0L, "2번");
        Product product3 = Product.create(null,  "상품3", 3000L, false, 100L, "썸네일3", 0L, "3번");
        Product product4 = Product.create(null,  "상품4", 4000L, false, 100L, "썸네일4", 0L, "4번");
        productRepository.saveAll(List.of(product1, product2, product3, product4));

        OrderDetail orderDetail1 = OrderDetail.create("tid1", null, product1, "orderNo1", null, 1000L, 1L, PAYMENT_COMPLETED.getCode() ,null);
        OrderDetail orderDetail2 = OrderDetail.create("tid1", null, product2, "orderNo1", null, 1000L, 1L,  PAYMENT_COMPLETED.getCode() ,null);
        OrderDetail orderDetail3 = OrderDetail.create("tid1", null, product3, "orderNo1", null, 1000L, 1L, PAYMENT_COMPLETED.getCode() ,null);

        OrderDetail orderDetail4= OrderDetail.create("tid2", null, product1, "orderNo2", null, 1000L, 2L,  PAYMENT_COMPLETED.getCode() ,null);
        OrderDetail orderDetail5 = OrderDetail.create("tid2", null, product2, "orderNo2", null, 1000L, 2L, PAYMENT_COMPLETED.getCode() ,null);
        OrderDetail orderDetail6 = OrderDetail.create("tid2", null, product4, "orderNo2", null, 1000L, 2L, PAYMENT_COMPLETED.getCode() ,null);

        OrderDetail orderDetail7 = OrderDetail.create("tid3", null, product1, "orderNo3", null, 1000L, 3L,  PAYMENT_COMPLETED.getCode() ,null);
        OrderDetail orderDetail8 = OrderDetail.create("tid3", null, product3, "orderNo3", null, 1000L, 3L, PAYMENT_COMPLETED.getCode() ,null);
        OrderDetail orderDetail9 = OrderDetail.create("tid3", null, product4, "orderNo3", null, 1000L, 3L,  PAYMENT_COMPLETED.getCode() ,null);

        OrderDetail orderDetail10 = OrderDetail.create("tid4", null, product2, "orderNo4", null, 1000L, 4L, PAYMENT_COMPLETED.getCode() ,null);
        OrderDetail orderDetail11 = OrderDetail.create("tid4", null, product3, "orderNo4", null, 1000L, 4L, PAYMENT_COMPLETED.getCode() ,null);
        OrderDetail orderDetail12 = OrderDetail.create("tid4", null, product4, "orderNo4", null, 1000L, 4L,  PAYMENT_COMPLETED.getCode() ,null);

        Payment payment1 = createPayment("orderNo1", "카드", "tid1", createCardInfo(CardCoType.KAKAOBANK.toString(), null, "0"), createAmount(3000L, 0L, 0L), LocalDateTime.of(2024,10,11,12,30,30));
        Payment payment2 = createPayment("orderNo2", "카드", "tid2", createCardInfo(CardCoType.KB.toString(), null, "0"), createAmount(3000L, 0L, 0L), LocalDateTime.of(2024,10,11,12,30,30));
        Payment payment3 = createPayment("orderNo3", "카드", "tid3", createCardInfo(CardCoType.SHINHAN.toString(),null, "0"), createAmount(3000L, 0L, 0L), LocalDateTime.of(2024,10,11,12,30,30));
        Payment payment4 = createPayment("orderNo4", "현금", "tid4", createCardInfo(null, null, "0"), createAmount(3000L, 0L, 0L), LocalDateTime.of(2024,10,11,12,30,30));

        Order order1 = createOrder(member, address, "상품1 외 2건", "orderNo1", 3000L, 3000L, 0L, "tid1", List.of(orderDetail1, orderDetail2, orderDetail3), payment1);
        Order order2 = createOrder(member, address, "상품1 외 2건", "orderNo2", 6000L, 6000L, 0L,"tid2", List.of(orderDetail4, orderDetail5, orderDetail6), payment2);
        Order order3 = createOrder(member, address, "상품1 외 2건", "orderNo3", 9000L, 9000L, 0L,"tid3", List.of(orderDetail7, orderDetail8, orderDetail9), payment3);
        Order order4 = createOrder(member, address, "상품2 외 2건", "orderNo4", 12000L, 12000L, 0L,"tid4", List.of(orderDetail10, orderDetail11, orderDetail12), payment4);

        orderCommandRepository.saveAll(List.of(order1, order2, order3, order4));
    }
    private Address createAddress(AddressValue addressValue, Boolean isDefault) {
        return Address.create(addressValue, isDefault);
    }

    private AddressValue createAddressValue(String phoneNo, String name, String city, String street, String zipcode, String detail, String alias) {
        return AddressValue.create(phoneNo, name, city, street, zipcode, detail, alias);
    }

    private Payment createPayment(String orderNo, String paymentMethod, String tid, CardInfo cardInfo, Amount amount, LocalDateTime approvedAt) {
        return Payment.create(orderNo, paymentMethod, tid, cardInfo, amount, approvedAt);
    }

    private Amount createAmount(Long totalAmount, Long discountAmount, Long taxFreeAmount) {
        return Amount.create(totalAmount, discountAmount, taxFreeAmount);
    }

    private CardInfo createCardInfo(String cardIssuerName, String cardPurchaseName, String cardInstallMonth) {
        return CardInfo.create(cardIssuerName, cardPurchaseName, cardInstallMonth);
    }

    private Order createOrder(Member member, Address address, String orderName, String orderNo, Long totalPrice, Long realPrice, Long totalDiscount, String tid, List<OrderDetail> orderDetails, Payment payment) {
        Order order = Order.create(member, address, orderName, orderNo, totalPrice, realPrice, totalDiscount, tid);

        // order <-> orderDetail 연관관계
        orderDetails.forEach(order::addOrderDetail);

        // payment <-> payment 연관관계
        order.linkPayment(payment);

        return order;
    }
    private GetOrderHttp.Condition createCondition(Long memberId, String keyword, String year) {
        return GetOrderHttp.Condition.builder()
                .memberId(memberId)
                .keyword(keyword)
                .year(year)
                .build();
    }
    @Test
    @DisplayName("전체 주문 조회시 상품정보에 대한 정보를 얻을 수 있다.")
    // TODO : 테스트 마무리해야함
    void search() {
        // given
        Member member = memberRepository.findByEmail("email.com");
        Pageable pageRequest1 = PageRequest.of(0, 1);
        GetOrderHttp.Condition condition
                = createCondition(member.getId(), "상품", String.valueOf(LocalDateTime.now().getYear()));

        // when
        PageResponse<GetOrderHttp.Response> response = getOrderService.search(pageRequest1, condition);

        //then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getOrderDetailInfos()).hasSize(3);
        assertThat(response.getContent().get(0).getOrderDetailInfos().get(0).getProductName()).isEqualTo("상품1");

    }
    @Test
    @DisplayName("전체 주문 조회시 페이징이 가능하다")
    void search_paging() {
        // given
        Member member = memberRepository.findByEmail("email.com");
        Pageable pageRequest1 = PageRequest.of(0, 4);
        Pageable pageRequest2 = PageRequest.of(0, 2);
        Pageable pageRequest3 = PageRequest.of(2, 2);
        GetOrderHttp.Condition condition
                = createCondition(member.getId(), "상품", String.valueOf(LocalDateTime.now().getYear()));

        // when
        PageResponse<GetOrderHttp.Response> response1 = getOrderService.search(pageRequest1, condition);
        PageResponse<GetOrderHttp.Response> response2 = getOrderService.search(pageRequest2, condition);
        PageResponse<GetOrderHttp.Response> response3 = getOrderService.search(pageRequest3, condition);

        //then
        assertThat(response1.getContent()).hasSize(4);
        assertThat(response2.getContent()).hasSize(2);
        assertThat(response3.getContent()).hasSize(0);
    }

    @DisplayName("주문 상세 조회")
    @Test
    void getOrderDetails(){
        //given
        String orderNo = "orderNo1";
        Order order = orderQueryRepository.findByOrderNo(orderNo);

        //when
        GetOrderDetailHttp.Response response = getOrderService.getOrderDetails(order.getId());

        //then
        assertThat(response.getOrderNo()).isEqualTo(orderNo);
        assertThat(response.getOrderDetailInfos()).hasSize(3);
        assertThat(response.getOrderDetailInfos().get(0).getProductName()).isEqualTo("상품1");
        assertThat(response.getAddressValue().getCity()).isEqualTo("서울");
        assertThat(response.getPaymentInfo().getCardIssuerName()).isEqualTo(CardCoType.KAKAOBANK.toString());
    }

}