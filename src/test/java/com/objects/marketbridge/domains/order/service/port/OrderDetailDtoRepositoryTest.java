package com.objects.marketbridge.domains.order.service.port;

import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.infra.dtio.GetCancelReturnListDtio;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.objects.marketbridge.domains.order.domain.StatusCodeType.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@Slf4j
class OrderDetailDtoRepositoryTest {


    @Autowired
    OrderCommandRepository orderCommandRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    OrderDetailDtoRepository orderDetailDtoRepository;
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("유저가 반품, 취소한 상품들을 조회할 수 있다.")
    public void findCancelReturnListDtio() {
        // given
        Member member = Member.builder().build();

        Order order1 = Order.builder()
                .member(member)
                .orderNo("123")
                .build();

        Order order2 = Order.builder()
                .member(member)
                .orderNo("456")
                .build();

        Product product1 = Product.builder()
                .productNo("1")
                .name("옷")
                .price(1000L)
                .build();
        Product product2 = Product.builder()
                .productNo("2")
                .name("신발")
                .price(2000L)
                .build();
        Product product3 = Product.builder()
                .productNo("3")
                .name("바지")
                .price(3000L)
                .build();

        OrderDetail orderDetail1 = OrderDetail.builder()
                .order(order1)
                .product(product1)
                .quantity(1L)
                .price(1000L)
                .orderNo("1")
                .statusCode(RETURN_COMPLETED.getCode())
                .build();
        OrderDetail orderDetail2 = OrderDetail.builder()
                .order(order1)
                .product(product2)
                .quantity(2L)
                .price(2000L)
                .orderNo("1")
                .statusCode(ORDER_CANCEL.getCode())
                .build();
        OrderDetail orderDetail3 = OrderDetail.builder()
                .order(order2)
                .product(product3)
                .quantity(3L)
                .price(3000L)
                .orderNo("2")
                .statusCode(ORDER_CANCEL.getCode())
                .build();
        OrderDetail orderDetail4 = OrderDetail.builder()
                .order(order2)
                .product(product2)
                .quantity(4L)
                .price(2000L)
                .orderNo("2")
                .statusCode(DELIVERY_ING.getCode())
                .build();

        order1.addOrderDetail(orderDetail1);
        order1.addOrderDetail(orderDetail2);
        order2.addOrderDetail(orderDetail3);
        order2.addOrderDetail(orderDetail4);

        productRepository.saveAll(List.of(product1, product2, product3));
        memberRepository.save(member);
        orderCommandRepository.save(order1);
        orderCommandRepository.save(order2);

        // when
        Page<GetCancelReturnListDtio.Response> orderCancelReturnListResponsePage = orderDetailDtoRepository.findCancelReturnListDtio(member.getId(), PageRequest.of(0, 3));
        List<GetCancelReturnListDtio.Response> content = orderCancelReturnListResponsePage.getContent();
        // then
        assertThat(content).hasSize(3);

        assertThat(content.get(0).getOrderDetailInfo().getOrderNo()).isEqualTo("1");
        assertThat(content.get(0).getOrderDetailInfo().getProductNo()).isEqualTo("1");
        assertThat(content.get(0).getOrderDetailInfo().getName()).isEqualTo("옷");
        assertThat(content.get(0).getOrderDetailInfo().getPrice()).isEqualTo(1000L);
        assertThat(content.get(0).getOrderDetailInfo().getQuantity()).isEqualTo(1L);
        assertThat(content.get(0).getOrderDetailInfo().getOrderStatus()).isEqualTo(RETURN_COMPLETED.getCode());

        assertThat(content.get(1).getOrderDetailInfo().getOrderNo()).isEqualTo("1");
        assertThat(content.get(1).getOrderDetailInfo().getProductNo()).isEqualTo("2");
        assertThat(content.get(1).getOrderDetailInfo().getName()).isEqualTo("신발");
        assertThat(content.get(1).getOrderDetailInfo().getPrice()).isEqualTo(2000L);
        assertThat(content.get(1).getOrderDetailInfo().getQuantity()).isEqualTo(2L);
        assertThat(content.get(1).getOrderDetailInfo().getOrderStatus()).isEqualTo(ORDER_CANCEL.getCode());

        assertThat(content.get(2).getOrderDetailInfo().getOrderNo()).isEqualTo("2");
        assertThat(content.get(2).getOrderDetailInfo().getProductNo()).isEqualTo("3");
        assertThat(content.get(2).getOrderDetailInfo().getName()).isEqualTo("바지");
        assertThat(content.get(2).getOrderDetailInfo().getPrice()).isEqualTo(3000L);
        assertThat(content.get(2).getOrderDetailInfo().getQuantity()).isEqualTo(3L);
        assertThat(content.get(2).getOrderDetailInfo().getOrderStatus()).isEqualTo(ORDER_CANCEL.getCode());
    }
}