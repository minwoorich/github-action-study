package com.objects.marketbridge.domains.payment.controller.dto;

import com.objects.marketbridge.domains.member.domain.AddressValue;
import com.objects.marketbridge.domains.payment.domain.CardInfo;
import com.objects.marketbridge.domains.payment.domain.Payment;
import com.objects.marketbridge.domains.payment.service.dto.ProductInfoDto;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CompleteOrderHttp {

    @Getter
    @NoArgsConstructor
    public static class Response {

        private String paymentMethodType;
        private String orderName;
        private String approvedAt;
        private Long totalAmount;
        private Long discountAmount;
        private Long taxFreeAmount;
        private String cardIssuerName;
        private Long cardInstallMonth;
        private AddressValue addressValue;
        private List<ProductInfoDto> productInfos;

        @Builder
        private Response(String paymentMethodType, String orderName, String approvedAt, Long totalAmount, Long discountAmount, Long taxFreeAmount, String cardIssuerName, Long cardInstallMonth, AddressValue addressValue, List<ProductInfoDto> productInfos) {
            this.paymentMethodType = paymentMethodType;
            this.orderName = orderName;
            this.approvedAt = approvedAt;
            this.totalAmount = totalAmount;
            this.discountAmount = discountAmount;
            this.taxFreeAmount = taxFreeAmount;
            this.cardIssuerName = cardIssuerName;
            this.cardInstallMonth = cardInstallMonth;
            this.addressValue = addressValue;
            this.productInfos = productInfos;
        }

        public static Response of(Payment payment) {
            CardInfo cardInfo = payment.getCardInfo();
            return Response.builder()
                    .paymentMethodType(payment.getPaymentMethod())
                    .orderName(payment.getOrder().getOrderName())
                    .approvedAt(payment.getApprovedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .cardIssuerName(cardInfo != null ? cardInfo.getCardIssuerName() : null)
                    .cardInstallMonth(cardInfo != null ? Long.parseLong(payment.getCardInfo().getCardInstallMonth()) : null)
                    .totalAmount(payment.getAmount().getTotalAmount())
                    .discountAmount(payment.getAmount().getDiscountAmount())
                    .taxFreeAmount(payment.getAmount().getTaxFreeAmount())
                    .addressValue(payment.getOrder().getAddress().getAddressValue())
                    .productInfos(createProductInfoDtos(payment.getOrder().getOrderDetails()))
                    .build();
        }

        public static Response create(String paymentMethodType, String orderName, String approvedAt, Long totalAmount, Long discountAmount, Long taxFreeAmount, String cardIssuerName, Long cardInstallMonth, AddressValue addressValue, List<ProductInfoDto> productInfos) {
            return Response.builder()
                    .paymentMethodType(paymentMethodType)
                    .orderName(orderName)
                    .approvedAt(approvedAt)
                    .totalAmount(totalAmount)
                    .discountAmount(discountAmount)
                    .taxFreeAmount(taxFreeAmount)
                    .cardIssuerName(cardIssuerName)
                    .cardInstallMonth(cardInstallMonth)
                    .addressValue(addressValue)
                    .productInfos(productInfos)
                    .build();
        }

        // TODO : productInfos -> sellerName,deliveredDate 미완성
        private static List<ProductInfoDto> createProductInfoDtos(List<OrderDetail> orderDetails) {
            return orderDetails.stream().map(o -> ProductInfoDto.of(o.getProduct())).collect(Collectors.toList());
        }
    }

}
