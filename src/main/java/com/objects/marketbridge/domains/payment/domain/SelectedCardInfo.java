package com.objects.marketbridge.domains.payment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SelectedCardInfo {
    @JsonProperty("card_bin")
    private String cardBin;

    @JsonProperty("install_month")
    private Long installMonth;

    @JsonProperty("card_corp_name")
    private String cardCorpName;

    @Builder
    private SelectedCardInfo(String cardBin, Long installMonth, String cardCorpName) {
        this.cardBin = cardBin;
        this.installMonth = installMonth;
        this.cardCorpName = cardCorpName;
    }

    public static SelectedCardInfo create(String cardBin, Long installMonth, String cardCorpName) {
        return SelectedCardInfo.builder()
                .cardBin(cardBin)
                .installMonth(installMonth)
                .cardCorpName(cardCorpName)
                .build();
    }
}
