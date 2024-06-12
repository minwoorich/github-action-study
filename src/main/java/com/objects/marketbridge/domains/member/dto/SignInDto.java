package com.objects.marketbridge.domains.member.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignInDto {
    @NotEmpty
    @Email()
    private String email;

    @NotEmpty
    @NotBlank()
    private String password;

    @Builder
    public SignInDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
