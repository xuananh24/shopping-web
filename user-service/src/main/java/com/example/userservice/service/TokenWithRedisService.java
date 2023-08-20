package com.example.userservice.service;

import java.util.List;

public interface TokenWithRedisService {
    void saveToken(String refreshToken, String accessToken);
    List<String> getToken(String refreshToken);
}
