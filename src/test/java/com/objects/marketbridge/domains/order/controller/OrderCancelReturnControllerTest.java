package com.objects.marketbridge.domains.order.controller;

import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.order.controller.dto.GetCancelReturnListHttp;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.mock.BaseFakeOrderDetailRepository;
import com.objects.marketbridge.domains.order.mock.BaseFakeOrderRepository;
import com.objects.marketbridge.domains.order.mock.TestContainer;
import com.objects.marketbridge.domains.order.mock.TestDateTimeHolder;
import com.objects.marketbridge.domains.product.domain.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static com.objects.marketbridge.domains.order.domain.StatusCodeType.ORDER_CANCEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

public class OrderCancelReturnControllerTest {

    private LocalDateTime orderDate = LocalDateTime.of(2024, 2, 9, 3, 9);
    private TestContainer testContainer = TestContainer.builder()
            .dateTimeHolder(
                    TestDateTimeHolder.builder()
                            .createTime(orderDate)
                            .build()
            )
            .build();

    @AfterEach
    void afterEach() {
        BaseFakeOrderDetailRepository.getInstance().clear();
        BaseFakeOrderRepository.getInstance().clear();
        testContainer.memberRepository.deleteAllInBatch();
        testContainer.productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("취소/반품한 상품들을 조회할 수 있다.")
    public void getCancelReturnList() {
        // given
        Member member = Member.builder()
                .membership(MembershipType.BASIC.getText())
                .build();

        Product product1 = Product.builder()
                .name("빵빵이키링")
                .thumbImg("빵빵이썸네일")
                .productNo("1")
                .build();
        Product product2 = Product.builder()
                .name("옥지얌키링")
                .thumbImg("옥지얌썸네일")
                .productNo("2")
                .build();

        LocalDateTime orderDate1 = LocalDateTime.of(2024, 2, 8, 9, 30);
        LocalDateTime orderDate2 = LocalDateTime.of(2024, 2, 8, 9, 31);
        LocalDateTime cancelDate1 = LocalDateTime.of(2024, 2, 8, 9, 32);
        LocalDateTime cancelDate2 = LocalDateTime.of(2024, 2, 8, 9, 33);

        Order order = Order.builder()
                .member(member)
                .orderNo("1")
                .build();

        OrderDetail orderDetail1 = OrderDetail.builder()
                .order(order)
                .orderNo("1")
                .quantity(10L)
                .product(product1)
                .price(1000L)
                .statusCode(ORDER_CANCEL.getCode())
                .cancelledAt(cancelDate1)
                .build();
        ReflectionTestUtils.setField(orderDetail1, "createdAt", orderDate1, LocalDateTime.class);
        OrderDetail orderDetail2 = OrderDetail.builder()
                .order(order)
                .orderNo("1")
                .quantity(10L)
                .product(product2)
                .price(2000L)
                .statusCode(ORDER_CANCEL.getCode())
                .cancelledAt(cancelDate2)
                .build();
        ReflectionTestUtils.setField(orderDetail2, "createdAt", orderDate2, LocalDateTime.class);

        order.addOrderDetail(orderDetail1);
        order.addOrderDetail(orderDetail2);

        testContainer.orderCommandRepository.save(order);
        testContainer.productRepository.save(product1);
        testContainer.productRepository.save(product2);
        testContainer.orderDetailCommandRepository.save(orderDetail1);
        testContainer.orderDetailCommandRepository.save(orderDetail2);
        testContainer.memberRepository.save(member);

        Integer page = 0;
        Integer size = 5;
        Long memberId = 1L;

        // when
        ApiResponse<Page<GetCancelReturnListHttp.Response>> result = testContainer.orderCancelReturnController.getCancelReturnList(page, size, memberId);

        // then
        assertThat(result.getCode()).isEqualTo(OK.value());
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessage()).isEqualTo(OK.name());
        assertThat(result.getData().getContent().size()).isEqualTo(2);
        assertThat(result.getData().getContent().get(0).getCancelReceiptDate()).isEqualTo(cancelDate1);
        assertThat(result.getData().getContent().get(0).getOrderDate()).isEqualTo(orderDate1);
        assertThat(result.getData().getContent().get(1).getCancelReceiptDate()).isEqualTo(cancelDate2);
        assertThat(result.getData().getContent().get(1).getOrderDate()).isEqualTo(orderDate2);

        GetCancelReturnListHttp.OrderDetailInfo orderDetailInfo1 = result.getData().getContent().get(0).getOrderDetailInfo();
        assertThat(orderDetailInfo1.getOrderNo()).isEqualTo("1");
        assertThat(orderDetailInfo1.getProductId()).isEqualTo(1L);
        assertThat(orderDetailInfo1.getProductNo()).isEqualTo("1");
        assertThat(orderDetailInfo1.getName()).isEqualTo("빵빵이키링");
        assertThat(orderDetailInfo1.getPrice()).isEqualTo(1000L);
        assertThat(orderDetailInfo1.getQuantity()).isEqualTo(10L);
        assertThat(orderDetailInfo1.getOrderStatus()).isEqualTo(ORDER_CANCEL.getCode());

        GetCancelReturnListHttp.OrderDetailInfo orderDetailInfo2 = result.getData().getContent().get(1).getOrderDetailInfo();
        assertThat(orderDetailInfo2.getOrderNo()).isEqualTo("1");
        assertThat(orderDetailInfo2.getProductId()).isEqualTo(2L);
        assertThat(orderDetailInfo2.getProductNo()).isEqualTo("2");
        assertThat(orderDetailInfo2.getName()).isEqualTo("옥지얌키링");
        assertThat(orderDetailInfo2.getPrice()).isEqualTo(2000L);
        assertThat(orderDetailInfo2.getQuantity()).isEqualTo(10L);
        assertThat(orderDetailInfo2.getOrderStatus()).isEqualTo(ORDER_CANCEL.getCode());
    }

}
