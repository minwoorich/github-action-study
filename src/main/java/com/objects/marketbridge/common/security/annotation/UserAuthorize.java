package com.objects.marketbridge.common.security.annotation;

import com.objects.marketbridge.common.security.constants.SecurityConst;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(SecurityConst.AUTH_USER)
public @interface UserAuthorize {
}
