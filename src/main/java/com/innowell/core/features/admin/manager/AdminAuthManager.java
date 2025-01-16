package com.innowell.core.features.admin.manager;


import com.innowell.core.features.auth.dto.TokenResponseDto;
import com.innowell.core.features.auth.service.AuthTokenService;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthManager {

    @Autowired
    AuthTokenService authTokenService;

    public TokenResponseDto loginIntoAdminClient(String code, String state) {

        OAuthJSONAccessTokenResponse tokenResponseDto = authTokenService.getOAuthToken(code, "f9e5feced85b9017768a", "0d1ef57a14fb36403146a1180974f8017f8ba138", state);
        return new TokenResponseDto(tokenResponseDto.getAccessToken(), tokenResponseDto.getTokenType(), tokenResponseDto.getExpiresIn(), tokenResponseDto.getScope(), tokenResponseDto.getRefreshToken());

    }
}
