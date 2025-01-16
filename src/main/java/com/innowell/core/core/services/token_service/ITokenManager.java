package com.innowell.core.core.services.token_service;

public interface ITokenManager {
    /**
     * Retrieves the access token for a specific API.
     *
     * @param apiName - The name of the API for which the token is needed.
     * @return A Mono containing the access token as a String.
     */
    String getToken(String apiName, String authUrl);
}
