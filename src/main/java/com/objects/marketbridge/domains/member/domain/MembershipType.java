package com.objects.marketbridge.domains.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MembershipType {
    BASIC("일반"),
    WOW("와우");

    private final String text;
}
