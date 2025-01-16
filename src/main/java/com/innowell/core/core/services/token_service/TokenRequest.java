package com.innowell.core.core.services.token_service;

class TokenRequest {
    private final String clientAPIKey;
    private final String clientKey;

    public TokenRequest(String clientAPIKey, String clientKey) {
        this.clientAPIKey = clientAPIKey;
        this.clientKey = clientKey;
    }

    // getters and setters (not shown here, but assumed to exist)
}