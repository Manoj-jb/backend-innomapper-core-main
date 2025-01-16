package com.innowell.core.features.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String scope;
    private String refreshToken;
}