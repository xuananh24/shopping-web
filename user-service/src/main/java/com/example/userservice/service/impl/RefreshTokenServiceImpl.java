package com.example.userservice.service.impl;

import com.example.userservice.common.constant.TokenConstant;
import com.example.userservice.model.entity.RefreshToken;
import com.example.userservice.model.entity.User;
import com.example.userservice.repository.RefreshTokenRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.RefreshTokenService;
import com.example.userservice.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private Long refreshTokenDuration = TokenConstant.REFRESH_TOKEN_DURATION;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, JwtUtils jwtUtils) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token).get();
    }

    @Override
    public RefreshToken createRefreshToken(Long userId) {
        if (!checkTokenByUser(userId)) {
            RefreshToken refreshToken = RefreshToken.builder()
                    .user(userRepository.findById(userId).get())
                    .expireDate(LocalDateTime.now().plusSeconds(refreshTokenDuration))
                    .token(UUID.randomUUID().toString())
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
        return refreshTokenRepository.findByUserId(userId).get();
    }

    @Override
    public void removeByUserId(Long userId) {
        refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    @Override
    public boolean checkTokenByUser(Long userId) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId).orElse(null);
        if (refreshToken != null) {
            if (refreshToken.getExpireDate().compareTo(LocalDateTime.now()) < 0) {
                refreshTokenRepository.delete(refreshToken);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean verifyToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElse(null);
        if (refreshToken != null && refreshToken.getExpireDate().compareTo(LocalDateTime.now()) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public String getJwtToken(String token) {
        if (verifyToken(token)) {
            User user = refreshTokenRepository.findByToken(token).get().getUser();
            return jwtUtils.createToken(user.getUsername(), user.getRole(), user.getStatus());
        }
        return null;
    }


}
