package com.example.sharedfilter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ApiKeyAuthenticationFilter implements Filter {
    private String apiKey;

    public ApiKeyAuthenticationFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String providedApiKey = request.getHeader("API-Key");

        if (providedApiKey != null && providedApiKey.equals(apiKey)) {
            filterChain.doFilter(request, response);
        } else {
            response.getWriter().write("Unauthorized");
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
