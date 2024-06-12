package com.objects.marketbridge.domains.payment.controller.dto;

import com.objects.marketbridge.common.kakao.dto.KakaoPayOrderResponse;
import com.objects.marketbridge.domains.payment.domain.SelectedCardInfo;
import com.objects.marketbridge.domains.payment.service.dto.ProductInfoDto;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CancelledPaymentHttp {

    @Getter
    @NoArgsConstructor
    public static class Response{

        private String paymentMethodType;
        private String orderName;
        private String canceledAt;
        private String kakaoStatus;
        private Long totalAmount;
        private Long discountAmount;
        private Long taxFreeAmount;
        private String cardIssuerName;
        private Long cardInstallMonth;
        private List<ProductInfoDto> productInfos;

        @Builder
        private Response(String paymentMethodType, String orderName,  String canceledAt, String kakaoStatus, Long totalAmount, Long discountAmount, Long taxFreeAmount, String cardIssuerName,  Long cardInstallMonth, List<ProductInfoDto> productInfos) {
            this.paymentMethodType = paymentMethodType;
            this.orderName = orderName;
            this.canceledAt = canceledAt;
            this.kakaoStatus = kakaoStatus;
            this.totalAmount = totalAmount;
            this.discountAmount = discountAmount;
            this.taxFreeAmount = taxFreeAmount;
            this.cardIssuerName = cardIssuerName;
            this.cardInstallMonth = cardInstallMonth;
            this.productInfos = productInfos;
        }

        public static CancelledPaymentHttp.Response of(KakaoPayOrderResponse kakaoResp, Order order) {
            // null 처리를 위한 로직
            SelectedCardInfo cardInfo = filterCardInfo(kakaoResp.getSelectedCardInfo());
            String canceledAt = filterCanceledAt(kakaoResp.getCanceledAt());
            return Response.builder()
                    .paymentMethodType(kakaoResp.getPaymentMethodType())
                    .orderName(kakaoResp.getItemName())
                    .canceledAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .canceledAt(canceledAt)
                    .kakaoStatus(kakaoResp.getKakaoStatus())
                    .cardInstallMonth(cardInfo.getInstallMonth())
                    .cardIssuerName(cardInfo.getCardCorpName())
                    .totalAmount(kakaoResp.getAmount().getTotalAmount())
                    .discountAmount(kakaoResp.getAmount().getDiscountAmount())
                    .taxFreeAmount(kakaoResp.getAmount().getTaxFreeAmount())
                    .productInfos(createProductInfoDtos(order.getOrderDetails()))
                    .build();
        }

        private static SelectedCardInfo filterCardInfo(SelectedCardInfo cardInfo) {
            if (cardInfo != null) {
                return SelectedCardInfo.create(cardInfo.getCardBin(), cardInfo.getInstallMonth(), cardInfo.getCardCorpName());
            }
            return SelectedCardInfo.create(null, null, null);
        }

        private static String filterCanceledAt(LocalDateTime canceledAt) {
            if (canceledAt != null) {
                return canceledAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            return null;
        }


        public static Response create(String paymentMethodType, String orderName,  String canceledAt, String kakaoStatus, Long totalAmount, Long discountAmount, Long taxFreeAmount, String cardIssuerName, Long cardInstallMonth,  List<ProductInfoDto> productInfos) {
            return Response.builder()
                    .paymentMethodType(paymentMethodType)
                    .orderName(orderName)
                    .canceledAt(canceledAt)
                    .kakaoStatus(kakaoStatus)
                    .totalAmount(totalAmount)
                    .discountAmount(discountAmount)
                    .taxFreeAmount(taxFreeAmount)
                    .cardIssuerName(cardIssuerName)
                    .cardInstallMonth(cardInstallMonth)
                    .productInfos(productInfos)
                    .build();
        }

        // TODO : productInfos -> sellerName,deliveredDate 미완성
        private static List<ProductInfoDto> createProductInfoDtos(List<OrderDetail> orderDetails) {
            return orderDetails.stream().map(o -> ProductInfoDto.of(o.getProduct())).collect(Collectors.toList());
        }
    }
}
