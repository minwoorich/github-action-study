package com.objects.marketbridge.domains.order.service.dto;

import com.objects.marketbridge.domains.member.domain.AddressValue;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.payment.domain.CardInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetOrderDto {

    private Long orderId;
    private Long memberId;
    private AddressValue address;
    private String orderName;
    private String orderNo;
    private Long totalDiscount; // 총 할인 금액 (쿠폰,포인트,멤버쉽)
    private Long totalPrice; // 총 금액
    private Long realPrice; // 실 결제 금액
    private LocalDateTime createdAt; // 주문 생성 시간
    private String paymentMethod;
    private String cardIssuerName;

    private List<GetOrderDetailDto> orderDetails;

    @Builder
    private GetOrderDto(Long orderId, Long memberId, AddressValue address, String orderName, String orderNo, Long totalDiscount, Long totalPrice, Long realPrice, LocalDateTime createdAt, String paymentMethod, String cardIssuerName, List<GetOrderDetailDto> orderDetails) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.address = address;
        this.orderName = orderName;
        this.orderNo = orderNo;
        this.totalDiscount = totalDiscount;
        this.totalPrice = totalPrice;
        this.realPrice = realPrice;
        this.createdAt = createdAt;
        this.paymentMethod = paymentMethod;
        this.cardIssuerName = cardIssuerName;
        this.orderDetails = orderDetails;
    }

    public static GetOrderDto of(Order order) {
        CardInfo cardInfo = order.getPayment().getCardInfo();

        return GetOrderDto.builder()
                .orderId(order.getId())
                .memberId(order.getMember().getId())
                .address(order.getAddress().getAddressValue())
                .orderName(order.getOrderName())
                .orderNo(order.getOrderNo())
                .totalDiscount(order.getTotalDiscount())
                .totalPrice(order.getTotalPrice())
                .realPrice(order.getRealPrice())
                .createdAt(order.getCreatedAt())
                .paymentMethod(order.getPayment().getPaymentMethod())
                .cardIssuerName(cardInfo != null ? cardInfo.getCardIssuerName() : null)
                .orderDetails(order.getOrderDetails().stream().map(GetOrderDetailDto::of).collect(Collectors.toList()))
                .build();
    }
}
