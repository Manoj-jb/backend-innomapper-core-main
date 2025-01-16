package com.innowell.core.core.services.token_service;

import lombok.Data;

@Data
class TokenResponse {
    public ResponseData responseData;

    // getters and setters (not shown here, but assumed to exist)
    @Data
    static class ResponseData {
        private String accessToken;
        private int expiresIn;
        private String tokenType;

        // getters and setters (not shown here, but assumed to exist)
    }
}
