package com.albany.vsm.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TokenResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    
    public TokenResponse(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
    }
}