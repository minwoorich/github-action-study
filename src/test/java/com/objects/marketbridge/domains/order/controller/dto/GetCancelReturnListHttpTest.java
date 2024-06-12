package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.domains.order.controller.dto.GetCancelReturnListHttp;
import com.objects.marketbridge.domains.order.infra.dtio.GetCancelReturnListDtio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static com.objects.marketbridge.domains.order.domain.StatusCodeType.ORDER_CANCEL;
import static org.assertj.core.api.Assertions.assertThat;

class GetCancelReturnListHttpTest {

    @Test
    @DisplayName("dtio.Response 주어지면 Http.Response 변환한다.")
    public void response_of() {
        // given
        LocalDateTime cancelReceiptDate = LocalDateTime.of(2024, 2, 8, 4, 59);
        LocalDateTime orderDate = LocalDateTime.of(2024, 2, 8, 4, 58);

        GetCancelReturnListDtio.OrderDetailInfo orderDetailInfo = GetCancelReturnListDtio.OrderDetailInfo.builder()
                .orderNo("1")
                .productId(1L)
                .productNo("1")
                .name("빵빵이키링")
                .price(1000L)
                .quantity(2L)
                .orderStatus(ORDER_CANCEL.getCode())
                .build();

        GetCancelReturnListDtio.Response response = GetCancelReturnListDtio.Response.builder()
                .cancelReceiptDate(cancelReceiptDate)
                .orderDate(orderDate)
                .orderDetailInfo(orderDetailInfo)
                .build();

        // when
        GetCancelReturnListHttp.Response result = GetCancelReturnListHttp.Response.of(response);

        // then
        assertThat(result.getCancelReceiptDate()).isEqualTo(cancelReceiptDate);
        assertThat(result.getOrderDate()).isEqualTo(orderDate);
        assertThat(result.getOrderDetailInfo().getOrderNo()).isEqualTo("1");
        assertThat(result.getOrderDetailInfo().getProductId()).isEqualTo(1L);
        assertThat(result.getOrderDetailInfo().getProductNo()).isEqualTo("1");
        assertThat(result.getOrderDetailInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getOrderDetailInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getOrderDetailInfo().getQuantity()).isEqualTo(2L);
        assertThat(result.getOrderDetailInfo().getOrderStatus()).isEqualTo(ORDER_CANCEL.getCode());
    }

    @Test
    @DisplayName("(page)dtio.Response 주어지면 Http.Response 변환한다.")
    public void response_of_page() {
        // given
        LocalDateTime cancelReceiptDate1 = LocalDateTime.of(2024, 2, 8, 4, 59);
        LocalDateTime orderDate1 = LocalDateTime.of(2024, 2, 8, 4, 58);

        GetCancelReturnListDtio.OrderDetailInfo orderDetailInfo1 = GetCancelReturnListDtio.OrderDetailInfo.builder()
                .orderNo("1")
                .productId(1L)
                .productNo("1")
                .name("빵빵이키링")
                .price(1000L)
                .quantity(2L)
                .orderStatus(ORDER_CANCEL.getCode())
                .build();

        GetCancelReturnListDtio.Response response1 = GetCancelReturnListDtio.Response.builder()
                .cancelReceiptDate(cancelReceiptDate1)
                .orderDate(orderDate1)
                .orderDetailInfo(orderDetailInfo1)
                .build();

        LocalDateTime cancelReceiptDate2 = LocalDateTime.of(2024, 2, 8, 4, 59);
        LocalDateTime orderDate2 = LocalDateTime.of(2024, 2, 8, 4, 58);

        GetCancelReturnListDtio.OrderDetailInfo orderDetailInfo2 = GetCancelReturnListDtio.OrderDetailInfo.builder()
                .orderNo("2")
                .productId(2L)
                .productNo("2")
                .name("옥지얌키링")
                .price(2000L)
                .quantity(3L)
                .orderStatus(ORDER_CANCEL.getCode())
                .build();

        GetCancelReturnListDtio.Response response2 = GetCancelReturnListDtio.Response.builder()
                .cancelReceiptDate(cancelReceiptDate2)
                .orderDate(orderDate2)
                .orderDetailInfo(orderDetailInfo2)
                .build();

        List<GetCancelReturnListDtio.Response> responses = List.of(response1, response2);

        PageImpl<GetCancelReturnListDtio.Response> given = new PageImpl<>(responses, PageRequest.of(0, 3), 2);

        // when
        Page<GetCancelReturnListHttp.Response> result = GetCancelReturnListHttp.Response.of(given);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getContent().get(0).getCancelReceiptDate()).isEqualTo(cancelReceiptDate1);
        assertThat(result.getContent().get(0).getOrderDate()).isEqualTo(orderDate1);
        assertThat(result.getContent().get(0).getOrderDetailInfo().getOrderNo()).isEqualTo("1");
        assertThat(result.getContent().get(0).getOrderDetailInfo().getProductId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getOrderDetailInfo().getProductNo()).isEqualTo("1");
        assertThat(result.getContent().get(0).getOrderDetailInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getContent().get(0).getOrderDetailInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getContent().get(0).getOrderDetailInfo().getQuantity()).isEqualTo(2L);
        assertThat(result.getContent().get(0).getOrderDetailInfo().getOrderStatus()).isEqualTo(ORDER_CANCEL.getCode());

        assertThat(result.getContent().get(1).getCancelReceiptDate()).isEqualTo(cancelReceiptDate2);
        assertThat(result.getContent().get(1).getOrderDate()).isEqualTo(orderDate2);
        assertThat(result.getContent().get(1).getOrderDetailInfo().getOrderNo()).isEqualTo("2");
        assertThat(result.getContent().get(1).getOrderDetailInfo().getProductId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getOrderDetailInfo().getProductNo()).isEqualTo("2");
        assertThat(result.getContent().get(1).getOrderDetailInfo().getName()).isEqualTo("옥지얌키링");
        assertThat(result.getContent().get(1).getOrderDetailInfo().getPrice()).isEqualTo(2000L);
        assertThat(result.getContent().get(1).getOrderDetailInfo().getQuantity()).isEqualTo(3L);
        assertThat(result.getContent().get(1).getOrderDetailInfo().getOrderStatus()).isEqualTo(ORDER_CANCEL.getCode());
    }

    @Test
    @DisplayName("dtio.OrderDetailInfo가 주어지면 Http.OrderDetailInfo로 변환한다.")
    public void orderDetailInfo_of() {
        // given
        GetCancelReturnListDtio.OrderDetailInfo dtio = GetCancelReturnListDtio.OrderDetailInfo.builder()
                .orderNo("1")
                .productId(1L)
                .productNo("1")
                .name("빵빵이키링")
                .price(1000L)
                .quantity(2L)
                .orderStatus(ORDER_CANCEL.getCode())
                .build();

        // when
        GetCancelReturnListHttp.OrderDetailInfo result = GetCancelReturnListHttp.OrderDetailInfo.of(dtio);

        // then
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getProductNo()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("빵빵이키링");
        assertThat(result.getPrice()).isEqualTo(1000L);
        assertThat(result.getQuantity()).isEqualTo(2L);
        assertThat(result.getOrderStatus()).isEqualTo(ORDER_CANCEL.getCode());
    }

}