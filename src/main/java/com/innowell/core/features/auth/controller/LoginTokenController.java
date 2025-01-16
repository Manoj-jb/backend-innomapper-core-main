package com.innowell.core.features.auth.controller;

import com.innowell.core.features.auth.dto.TokenResponseDto;
import com.innowell.core.features.auth.manager.AuthTokenManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/innowell-mapper/auth/token")
public class LoginTokenController {
    private final AuthTokenManager authTokenManager;

    public LoginTokenController(AuthTokenManager authTokenManager) {
        this.authTokenManager = authTokenManager;
    }

    @GetMapping("/login")
    public ResponseEntity<TokenResponseDto> loginIntoClient(@RequestParam("code") String code, @RequestParam("state") String state) {
        TokenResponseDto tokenInfo = authTokenManager.loginIntoClient(code, state);
        return ResponseEntity.ok(tokenInfo);
    }

    @GetMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestParam("refreshToken") String refreshToken, @RequestParam("clientName") String clientName) {
        TokenResponseDto tokenInfo = authTokenManager.refreshToken(refreshToken, clientName);
        return ResponseEntity.ok(tokenInfo);
    }

    @GetMapping("/my-profile")
    public ResponseEntity<String> getMyProfile(@RequestParam String clientName, @RequestParam String token) {
        return ResponseEntity.ok(authTokenManager.getMyProfile(token, clientName));
    }
}