package com.objects.marketbridge.domains.member.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckedResultDto {
    private Boolean checked;

    @Builder
    public CheckedResultDto(Boolean checked) {
        this.checked = checked;
    }

    public Boolean isChecked() {
        return checked;
    }
}
