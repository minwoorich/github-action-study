package com.objects.marketbridge.domains.member.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishlistRequest {

    private Long productId;

    @Builder
    public WishlistRequest(Long productId) {
        this.productId = productId;

    }
}

