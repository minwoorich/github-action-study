package com.objects.marketbridge.common.config;

import ch.qos.logback.classic.selector.servlet.LoggerContextFilter;
import com.objects.marketbridge.common.logging.LoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();

        // 1) loggingFilter 인스턴스 생성
        LoggingFilter loggingFilter = new LoggingFilter();

        // 2) loggingFilter 등록
        registrationBean.setFilter(loggingFilter);

        // 3) 필터 우선순위 적용
        registrationBean.setOrder(Integer.MIN_VALUE);

        // 4) 적용할 url
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
