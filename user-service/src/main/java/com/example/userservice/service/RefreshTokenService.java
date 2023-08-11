package com.example.userservice.service;

import com.example.userservice.model.entity.RefreshToken;
import com.example.userservice.model.entity.User;

public interface RefreshTokenService {
    RefreshToken findByToken(String token);
    RefreshToken createRefreshToken(Long userId);
    void removeByUserId(Long userId);
    boolean checkTokenByUser(Long userId);

    boolean verifyToken(String token);

    String getJwtToken(String token);
}
