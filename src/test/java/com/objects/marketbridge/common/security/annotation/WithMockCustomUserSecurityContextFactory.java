package com.objects.marketbridge.common.security.annotation;

import com.objects.marketbridge.common.security.domain.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        CustomUserDetails principal = new CustomUserDetails(annotation.id(), annotation.email(),null, List.of("USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, "", List.of(new SimpleGrantedAuthority(("ROLE_USER"))));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);

        return context;
    }
}
