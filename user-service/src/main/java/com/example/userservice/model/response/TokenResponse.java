package com.example.userservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TokenResponse {
    private String jwtToken;
    private String refreshToken;
}
