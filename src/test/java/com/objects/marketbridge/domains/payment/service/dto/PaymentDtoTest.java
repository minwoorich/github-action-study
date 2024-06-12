package com.objects.marketbridge.domains.payment.service.dto;

import com.objects.marketbridge.common.kakao.dto.KakaoPayApproveResponse;
import com.objects.marketbridge.domains.payment.domain.Amount;
import com.objects.marketbridge.domains.payment.service.dto.PaymentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentDtoTest {

    @Test
    @DisplayName("KakaoPayResponse를 PaymentDto로 변환한다.")
    public void of() {
        // given
        Amount amount = Amount.builder()
                .taxFreeAmount(0L)
                .totalAmount(3000L)
                .discountAmount(0L)
                .build();

        KakaoPayApproveResponse kakaoResponse = KakaoPayApproveResponse.builder()
                .orderName("빵빵이키링")
                .quantity(3L)
                .amount(amount)
                .build();

        // when
        PaymentDto result = PaymentDto.of(kakaoResponse);

        // then
        assertThat(result.getQuantity()).isEqualTo(3L);
        assertThat(result.getOrderName()).isEqualTo("빵빵이키링");
        assertThat(result.getDiscountAmount()).isEqualTo(0L);
        assertThat(result.getTotalAmount()).isEqualTo(3000L);
        assertThat(result.getTaxFreeAmount()).isEqualTo(0L);
    }

}