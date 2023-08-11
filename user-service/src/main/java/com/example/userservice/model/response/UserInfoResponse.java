package com.example.userservice.model.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponse {
    private Long id;

    private String name;

    private String email;

    private String status;
}
