package com.objects.marketbridge.domains.member.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdatePassword(@NotNull Long memberId, @NotEmpty @Size(min=4) String password) {
}
