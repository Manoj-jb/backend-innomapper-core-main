package com.innowell.core.features.auth.service;

import com.innowell.core.features.auth.dto.LoginUrlDto;
import com.innowell.core.features.auth.entity.LoginUrl;
import com.innowell.core.features.auth.repository.LoginUrlRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginUrlService {
    private final LoginUrlRepository loginUrlRepository;

    public LoginUrlService(LoginUrlRepository loginUrlRepository) {
        this.loginUrlRepository = loginUrlRepository;
    }

    public LoginUrlDto getLoginUrlByClientName(String clientName) {
        LoginUrl loginUrl = loginUrlRepository.findByClientName(clientName)
                .orElseThrow(() -> new RuntimeException("Login URL not found for client: " + clientName));

        LoginUrlDto dto = new LoginUrlDto();
        dto.setClientName(loginUrl.getClientName());
        dto.setLoginUrl(loginUrl.getLoginUrl());

        return dto;
    }
}