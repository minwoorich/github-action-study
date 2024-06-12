package com.objects.marketbridge.domains.order.controller.dto.select;

import com.objects.marketbridge.domains.member.domain.AddressValue;
import com.objects.marketbridge.domains.order.service.dto.GetOrderDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class GetOrderDetailHttp {

    @Getter
    @NoArgsConstructor
    public static class Response{
        private String orderNo;
        private String createdAt;
        private List<OrderDetailInfo> orderDetailInfos;
        private AddressValue addressValue;
        private PaymentInfo paymentInfo;

        @Builder
        public Response(String orderNo, String createdAt, List<OrderDetailInfo> orderDetailInfos, AddressValue addressValue, PaymentInfo paymentInfo) {
            this.orderNo = orderNo;
            this.createdAt = createdAt;
            this.orderDetailInfos = orderDetailInfos;
            this.addressValue = addressValue;
            this.paymentInfo = paymentInfo;
        }

        public static Response of(GetOrderDto getOrderDto) {
            return Response.builder()
                    .orderNo(getOrderDto.getOrderNo())
                    .createdAt(getOrderDto.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .orderDetailInfos(getOrderDto.getOrderDetails().stream().map(OrderDetailInfo::of).collect(Collectors.toList()))
                    .addressValue(getOrderDto.getAddress())
                    .paymentInfo(PaymentInfo.of(getOrderDto))
                    .build();
        }
    }

}
