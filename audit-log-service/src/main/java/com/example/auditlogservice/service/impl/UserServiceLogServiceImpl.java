package com.example.auditlogservice.service.impl;

import com.example.auditlogservice.model.dto.UserLogReceive;
import com.example.auditlogservice.model.entity.UserServiceLog;
import com.example.auditlogservice.repository.UserServiceLogRepository;
import com.example.auditlogservice.service.UserServiceLogService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserServiceLogServiceImpl implements UserServiceLogService {
    private final UserServiceLogRepository userServiceLogRepository;

    public UserServiceLogServiceImpl(UserServiceLogRepository userServiceLogRepository) {
        this.userServiceLogRepository = userServiceLogRepository;
    }

    @Override
    @KafkaListener(topics = "${user-service-log.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void save(UserLogReceive userLogReceive) {
        UserServiceLog userServiceLog = UserServiceLog.builder()
                .userId(userLogReceive.getUserId())
                .actionType(userLogReceive.getAction())
                .actionData(userLogReceive.getActionData())
                .timestamp(userLogReceive.getTimestamp())
                .build();
        userServiceLogRepository.save(userServiceLog);
    }
}
