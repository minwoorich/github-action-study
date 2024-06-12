package com.objects.marketbridge.domains.cart.controller.dto;

import com.objects.marketbridge.domains.cart.service.dto.CreateCartDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CreateCartHttp {

    @Getter
    @NoArgsConstructor
    public static class Request{

        @NotNull
        private Long productId;

        @Min(value = 1, message = "장바구니 수량은 1 이상 100 이하여야합니다")
        @Max(value = 100, message = "장바구니 수량은 1 이상 100 이하여야합니다")
        @NotNull
        private Long quantity;

        @NotNull
        private Boolean isSubs;

        @Builder
        private Request(Long productId, Long quantity,  Boolean isSubs) {
            this.productId = productId;
            this.quantity = quantity;
            this.isSubs = isSubs;
        }

        public static Request create(Long productId, Long quantity, Boolean isSubs) {
            return Request.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .isSubs(isSubs)
                    .build();
        }

        public CreateCartDto toDto(Long memberId) {
            return CreateCartDto.create(productId, memberId, quantity, isSubs);
        }
    }
}
