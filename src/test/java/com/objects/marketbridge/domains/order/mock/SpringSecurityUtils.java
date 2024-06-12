package com.objects.marketbridge.domains.order.mock;

import com.objects.marketbridge.common.security.domain.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

public class SpringSecurityUtils {
    public static Authentication authMemberId(CustomUserDetails userDetails) {
        UserDetails principal = new CustomUserDetails(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getPassword(),
                List.of("USER")
        );

        return new UsernamePasswordAuthenticationToken(principal, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
    }

}
