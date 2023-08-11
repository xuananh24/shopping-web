package com.example.auditlogservice.service;

import com.example.auditlogservice.model.dto.UserLogReceive;

public interface UserServiceLogService {
    void save(UserLogReceive userLogReceive);
}
