package com.objects.marketbridge.domains.member.dto;

import lombok.Builder;

@Builder
public record GetMemberInfo(String email,
                            String name,
                            String phoneNo,
                            Boolean isAgree,
                            Boolean isAlert) {
}
