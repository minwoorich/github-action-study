package com.objects.marketbridge.domains.member.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record UpdateMemberInfo(
    @NotEmpty
    String name,

    @NotEmpty
    @Size(min = 11)
    String phoneNo,

    @Nullable
    @Size(min = 4)
    String newPassword
) {
}