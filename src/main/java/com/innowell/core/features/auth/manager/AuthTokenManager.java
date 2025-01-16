package com.innowell.core.features.auth.manager;


import com.innowell.core.features.auth.dto.TokenResponseDto;
import com.innowell.core.features.auth.entity.LoginUrl;
import com.innowell.core.features.auth.entity.User;
import com.innowell.core.features.auth.exception.AuthException;
import com.innowell.core.features.auth.repository.LoginUrlRepository;
import com.innowell.core.features.auth.service.AuthTokenService;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthTokenManager {

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    LoginUrlRepository loginUrlRepository;

    public TokenResponseDto loginIntoClient(String code, String state) {
        Optional<LoginUrl> loginUrl = loginUrlRepository.findByClientName(state);

        if (!loginUrl.isPresent()) {
            throw new AuthException("Login Url not found");
        }

        OAuthJSONAccessTokenResponse tokenResponseDto = authTokenService.getOAuthToken(code, loginUrl.get().getClientId(), loginUrl.get().getClientSecret(), state);
        return new TokenResponseDto(tokenResponseDto.getAccessToken(), tokenResponseDto.getTokenType(), tokenResponseDto.getExpiresIn(), tokenResponseDto.getScope(), tokenResponseDto.getRefreshToken());
    }


    public TokenResponseDto refreshToken(String refreshToken, String clientName) {
        Optional<LoginUrl> loginUrl = loginUrlRepository.findByClientName(clientName);

        if (!loginUrl.isPresent()) {
            throw new AuthException("Login Url not found");
        }

        OAuthJSONAccessTokenResponse tokenResponseDto = authTokenService.getOAuthRefreshToken(loginUrl.get().getClientId(), loginUrl.get().getClientSecret(), clientName, refreshToken);
        return new TokenResponseDto(tokenResponseDto.getAccessToken(), tokenResponseDto.getTokenType(), tokenResponseDto.getExpiresIn(), tokenResponseDto.getScope(), tokenResponseDto.getRefreshToken());
    }

    public User testToken(String token, String clientName) {

        Optional<LoginUrl> loginUrl = loginUrlRepository.findByClientName(clientName);
        return authTokenService.parseJwtToken(token, loginUrl.get().getClientCertificate());
    }

    public String getMyProfile(String token, String clientName) {
        return authTokenService.getMyProfileUrl(token, null, clientName);
    }
}
