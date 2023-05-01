package com.blog.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTAuthResponse {
    private String accessToken;
    private static final String tokenType = "Bearer";

    public JWTAuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
