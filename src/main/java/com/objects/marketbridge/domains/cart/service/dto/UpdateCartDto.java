package com.objects.marketbridge.domains.cart.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCartDto {
    private Long cartId;
    private Long quantity;

    @Builder
    private UpdateCartDto(Long cartId, Long quantity) {
        this.cartId = cartId;
        this.quantity = quantity;
    }
}
