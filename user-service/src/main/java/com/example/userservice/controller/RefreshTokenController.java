package com.example.userservice.controller;

import com.example.userservice.common.constant.PathConstant;
import com.example.userservice.service.RefreshTokenService;
import com.example.userservice.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping(PathConstant.API_USER_GET_TOKEN_URL)
    public ResponseEntity<String> getToken(HttpServletRequest request) {
        String jwtToken = refreshTokenService.getJwtToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (jwtToken != null) {
            return ResponseEntity.ok(jwtToken);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
