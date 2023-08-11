package com.example.userservice.utils;

import com.example.userservice.common.constant.TokenConstant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private String secretKey = TokenConstant.SECRET_KEY;

    private Long expiration = TokenConstant.EXPIRATION;

    public String createToken(String username, String role, String status) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(System.currentTimeMillis() + expiration);
        String result =  Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("status", status)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        return result;
    }
}
