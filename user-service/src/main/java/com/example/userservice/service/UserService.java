package com.example.userservice.service;

import com.example.userservice.model.entity.User;
import com.example.userservice.model.request.UserInfoRequest;

import java.util.Optional;

public interface UserService {
    void register(UserInfoRequest userInfoRequest);

    void login(String username, String ipAddress);
    void sendVerifyCode(Long userId);
    boolean verifyByCode(Long userId, String code);

    Optional<User> findUserByUsername(String username);



}
