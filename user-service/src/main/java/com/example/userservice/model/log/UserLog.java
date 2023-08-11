package com.example.userservice.model.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLog {
    private Long userId;
    private String action;
    private String actionData;
    private LocalDateTime timestamp;
}
