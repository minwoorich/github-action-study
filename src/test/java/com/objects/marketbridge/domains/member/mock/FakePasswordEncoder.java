package com.objects.marketbridge.domains.member.mock;

import org.springframework.security.crypto.password.PasswordEncoder;


public class FakePasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return false;
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return true;
    }
}
