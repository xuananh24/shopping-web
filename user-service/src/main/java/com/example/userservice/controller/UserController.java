package com.example.userservice.controller;

import com.example.userservice.common.constant.PathConstant;
import com.example.userservice.model.entity.RefreshToken;
import com.example.userservice.model.entity.User;
import com.example.userservice.model.request.UserInfoRequest;
import com.example.userservice.model.response.TokenResponse;
import com.example.userservice.service.RefreshTokenService;
import com.example.userservice.service.TokenWithRedisService;
import com.example.userservice.service.UserService;
import com.example.userservice.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenWithRedisService tokenWithRedisService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, RefreshTokenService refreshTokenService, TokenWithRedisService tokenWithRedisService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.tokenWithRedisService = tokenWithRedisService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(value = PathConstant.API_USER_CREATE_URL)
    public void register(@RequestBody UserInfoRequest userInfoRequest) {
        userService.register(userInfoRequest);
    }

    @PostMapping(value = PathConstant.API_USER_SEND_VERIFY_CODE_URL)
    public void sendVerifyCode(@PathVariable Long userId) {
        userService.sendVerifyCode(userId);
    }

    @PostMapping(value = PathConstant.API_USER_VERIFY_CODE_URL)
    public boolean verifyByCode(@PathVariable(value = "userId") Long userId, @PathVariable(value = "code") String code) {
        return userService.verifyByCode(userId, code);
    }

    @PostMapping(value = PathConstant.API_USER_LOGIN_URL)
    public TokenResponse login(HttpServletRequest httpServletRequest) {
        String username = httpServletRequest.getUserPrincipal().getName();
        User user = userService.findUserByUsername(username).get();
        String ipAddress = httpServletRequest.getRemoteAddr();
        userService.login(username, ipAddress);
        String token = this.jwtUtils.createToken(username, user.getRole(), user.getStatus());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userService.findUserByUsername(username).get().getId());
        TokenResponse tokenResponse = TokenResponse.builder()
                .jwtToken(token)
                .refreshToken(refreshToken.getToken())
                .build();

//        tokenWithRedisService.saveToken(refreshToken.getToken(), token);

        return tokenResponse;
    }
}
