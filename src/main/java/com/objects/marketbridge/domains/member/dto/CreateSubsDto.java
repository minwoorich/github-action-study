package com.objects.marketbridge.domains.member.dto;

import com.objects.marketbridge.domains.payment.domain.Amount;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateSubsDto {

    private String tid;
    private Long memberId;
    private String subsOrderNo;
    private Amount amout;

    @Builder
    public CreateSubsDto(String tid, Long memberId, String subsOrderNo, Amount amout) {
        this.tid = tid;
        this.memberId = memberId;
        this.subsOrderNo = subsOrderNo;
        this.amout = amout;
    }

    public static CreateSubsDto of(Long totalPrice, Long memberId, String tid, String subsOrderNo) {
        return CreateSubsDto.builder()
                .memberId(memberId)
                .subsOrderNo(subsOrderNo)
                .amout(Amount.builder().totalAmount(totalPrice).build())
                .tid(tid)
                .build();
    }
}

