package com.example.productservice.security;

import com.example.productservice.common.constant.PathConstant;
import com.example.sharedfilter.ApiKeyAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    @Value("${service.product.apiKey}")
    private String apiKey;

    @Bean
    public FilterRegistrationBean<ApiKeyAuthenticationFilter> apiAuthenticationFilter() {
        FilterRegistrationBean<ApiKeyAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiKeyAuthenticationFilter(apiKey));
        registrationBean.addUrlPatterns(PathConstant.API_INTERNAL_URL);
        return registrationBean;
    }
}
