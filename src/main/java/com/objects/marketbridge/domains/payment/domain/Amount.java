package com.objects.marketbridge.domains.payment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Amount {

    @JsonProperty("total")
    private Long totalAmount;

    @JsonProperty("discount")
    private Long discountAmount;

    @JsonProperty("tax_free")
    private Long taxFreeAmount;

    @Builder
    public Amount(Long totalAmount, Long discountAmount, Long taxFreeAmount) {
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.taxFreeAmount=taxFreeAmount;
    }

    public static Amount create(Long totalAmount, Long discountAmount, Long taxFreeAmount) {
        return Amount.builder()
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .taxFreeAmount(taxFreeAmount)
                .build();
    }
}
