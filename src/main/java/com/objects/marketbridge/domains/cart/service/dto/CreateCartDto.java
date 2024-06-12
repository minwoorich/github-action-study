package com.objects.marketbridge.domains.cart.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCartDto {
    private Long productId;
    private Long memberId;
    private Long quantity;
    private Boolean isSubs;

    @Builder
    private CreateCartDto(Long productId, Long memberId, Long quantity, Boolean isSubs) {
        this.productId = productId;
        this.memberId = memberId;
        this.quantity = quantity;
        this.isSubs = isSubs;
    }

    public static CreateCartDto create(Long productId, Long memberId,Long quantity,  Boolean isSubs) {
        return CreateCartDto.builder()
                .productId(productId)
                .memberId(memberId)
                .quantity(quantity)
                .isSubs(isSubs)
                .build();
    }
}
