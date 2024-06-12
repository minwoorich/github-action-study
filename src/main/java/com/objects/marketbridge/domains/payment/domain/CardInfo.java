package com.objects.marketbridge.domains.payment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class CardInfo {
    //카카오페이 발급사명
    @JsonProperty("kakaopay_issuer_corp")
    private String cardIssuerName;
    //카카오페이 매입사명
    @JsonProperty("kakaopay_purchase_corp")
    private String cardPurchaseName;

    @JsonProperty("install_month")
    private String cardInstallMonth;

    @Builder
    public CardInfo(String cardIssuerName, String cardPurchaseName, String cardInstallMonth) {
        this.cardIssuerName = cardIssuerName;
        this.cardPurchaseName = cardPurchaseName;
        this.cardInstallMonth = cardInstallMonth;
    }

    public static CardInfo create(String cardIssuerName, String cardPurchaseName, String cardInstallMonth) {
        return CardInfo.builder()
                .cardIssuerName(cardIssuerName)
                .cardPurchaseName(cardPurchaseName)
                .cardInstallMonth(cardInstallMonth)
                .build();
    }
}
