package com.innowell.core.features.auth.controller;

import com.innowell.core.features.auth.dto.LoginUrlDto;
import com.innowell.core.features.auth.service.LoginUrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/innowell-mapper/auth/login-urls")
public class LoginUrlController {
    private final LoginUrlService loginUrlService;

    public LoginUrlController(LoginUrlService loginUrlService) {
        this.loginUrlService = loginUrlService;
    }

    @GetMapping("/{clientName}")
    public ResponseEntity<LoginUrlDto> getLoginUrlByClientName(@PathVariable String clientName) {
        LoginUrlDto loginUrlDto = loginUrlService.getLoginUrlByClientName(clientName);
        return ResponseEntity.ok(loginUrlDto);
    }
}