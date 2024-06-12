package com.objects.marketbridge.domains.cart.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class DeleteCartHttp {

    @Getter
    @NoArgsConstructor
    public static class Request {

        @NotNull
        private List<Long> selectedCartIds;

        @Builder
        private Request(List<Long> selectedCartIds) {
            this.selectedCartIds = selectedCartIds;
        }
    }
}
