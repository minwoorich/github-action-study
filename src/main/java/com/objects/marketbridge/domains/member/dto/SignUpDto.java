package com.objects.marketbridge.domains.member.dto;

import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.MembershipType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SignUpDto {

    @NotEmpty()
    @Email()
    private String email;

    @NotEmpty()
    @Size(min=4)
    private String password;

    @NotEmpty()
    private String name;

    @NotEmpty()
    @Size(min = 11)
    private String phoneNo;

    @NotNull
    private Boolean isAgree;

    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .email(email)
                .name(name)
                .password(encodedPassword)
                .phoneNo(phoneNo)
                .isAgree(isAgree)
                .isAlert(true)
                .membership(MembershipType.BASIC.toString())
                .build();
    }
}
