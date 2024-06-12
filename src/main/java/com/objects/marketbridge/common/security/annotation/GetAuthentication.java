package com.objects.marketbridge.common.security.annotation;

import com.objects.marketbridge.common.security.constants.SecurityConst;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(SecurityConst.AUTH_USER)
@AuthenticationPrincipal
public @interface GetAuthentication {
}
