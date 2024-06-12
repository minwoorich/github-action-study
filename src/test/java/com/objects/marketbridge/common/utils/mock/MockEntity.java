package com.objects.marketbridge.common.utils.mock;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MockEntity {
    private Long id;
    private String email;
    private String name;

    @Builder
    public MockEntity(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
