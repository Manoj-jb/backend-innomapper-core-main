package com.innowell.core.features.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowell.core.features.auth.entity.User;
import com.innowell.core.features.auth.exception.AuthException;
import com.innowell.core.features.auth.repository.LoginUrlRepository;
import com.innowell.core.features.auth.util.QueryUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;

@Service
@Slf4j
public class AuthTokenService {
    private final LoginUrlRepository loginUrlRepository;
    @Value("${casdoor.endpoint}")
    private String endpoint;

    public AuthTokenService(LoginUrlRepository loginUrlRepository) {
        this.loginUrlRepository = loginUrlRepository;
    }

    public OAuthJSONAccessTokenResponse getOAuthToken(String code, String clientId, String clientSecret, String clientName) {
        try {
            OAuthClientRequest oAuthClientRequest = OAuthClientRequest
                    .tokenLocation(String.format("%s/api/login/oauth/access_token", endpoint))
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(clientId)
                    .setParameter("client_name", clientName)
                    .setClientSecret(clientSecret)
                    .setRedirectURI(String.format("%s/api/login/oauth/authorize", endpoint))
                    .setCode(code)
                    .buildQueryMessage();
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

            return oAuthClient.accessToken(oAuthClientRequest, OAuth.HttpMethod.POST);
        } catch (OAuthSystemException | OAuthProblemException e) {
            throw new AuthException("Cannot get OAuth token.", e);
        }
    }


    public OAuthJSONAccessTokenResponse getOAuthRefreshToken(String clientId, String clientSecret, String clientName, String refreshToken) {
        try {
            OAuthClientRequest oAuthClientRequest = OAuthClientRequest
                    .tokenLocation(String.format("%s/api/login/oauth/refresh_token", endpoint))
                    .setGrantType(GrantType.REFRESH_TOKEN)
                    .setRefreshToken(refreshToken)
                    .setClientId(clientId)
                    .setScope("read openId profile email")
                    .setParameter("client_name", clientName)
                    .setClientSecret(clientSecret)
                    .buildQueryMessage();
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

            return oAuthClient.accessToken(oAuthClientRequest, OAuth.HttpMethod.POST);
        } catch (OAuthSystemException | OAuthProblemException e) {
            throw new AuthException("Cannot get OAuth token.", e);
        }
    }

    public User parseJwtToken(String token, String certificate) {
        ObjectMapper objectMapper = new ObjectMapper();
        // parse jwt token
        SignedJWT parseJwt = null;
        try {
            parseJwt = SignedJWT.parse(token);
        } catch (ParseException e) {
            throw new AuthException("Cannot parse jwt token.", e);
        }
        // verify the jwt public key
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificate.getBytes()));
            RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            boolean verify = parseJwt.verify(verifier);
            if (!verify) {
                throw new AuthException("Cannot verify signature.");
            }
        } catch (CertificateException | JOSEException e) {
            throw new AuthException("Cannot verify signature.", e);
        }

        // read "access_token" from payload and convert to CasdoorUser
        try {
            JWTClaimsSet claimsSet = parseJwt.getJWTClaimsSet();
            String userJson = claimsSet == null ? null : claimsSet.toString();

            if (userJson == null || userJson.isEmpty()) {
                throw new AuthException("Cannot get claims from JWT payload");
            }

            // check if the token has expired
            Date expireTime = claimsSet.getExpirationTime();
            if (expireTime.before(new Date())) {
                throw new AuthException("The token has expired");
            }

            return objectMapper.readValue(userJson, User.class);
        } catch (JsonProcessingException | java.text.ParseException e) {
            log.error("Cannot convert claims to User", e);
            throw new AuthException("Cannot convert claims to User", e);
        }
    }

    public String getUserProfileUrl(String username, String accessToken, String returnUrl, String clientName) {
        LinkedHashMap<String, Serializable> params = new LinkedHashMap<>();
        if (accessToken != null && accessToken.trim().length() > 0) params.put("access_token", accessToken);
        if (returnUrl != null && returnUrl.trim().length() > 0) params.put("returnUrl", returnUrl);
        if (username == null || username.trim().length() == 0) {
            return String.format("%s/account%s", endpoint, params.size() == 0 ? "" : "?" + QueryUtils.buildQuery(params));
        } else {
            return String.format("%s/users/%s/%s%s", endpoint, clientName, username, params.size() == 0 ? "" : "?" + QueryUtils.buildQuery(params));
        }
    }

    public String getMyProfileUrl(String accessToken, String returnUrl, String clientName) {
        return this.getUserProfileUrl(null, accessToken, returnUrl, clientName);
    }
}