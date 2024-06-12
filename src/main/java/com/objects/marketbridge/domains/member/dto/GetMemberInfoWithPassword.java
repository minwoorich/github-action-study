package com.objects.marketbridge.domains.member.dto;

import lombok.Builder;

@Builder
public record GetMemberInfoWithPassword(String email,
                                        String name,
                                        String password,
                                        String phoneNo,
                                        Boolean isAgree,
                                        Boolean isAlert) {
}
