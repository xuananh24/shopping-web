package com.example.auditlogservice.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLogReceive {
    private Long userId;
    private String action;
    private String actionData;
    private LocalDateTime timestamp;
}
