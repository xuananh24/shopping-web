package com.example.userservice.model.request;

import lombok.Data;


@Data
public class UserInfoRequest {
    private String name;

    private String email;

    private String username;

    private String password;

}
