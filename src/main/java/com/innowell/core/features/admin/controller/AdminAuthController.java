package com.innowell.core.features.admin.controller;

import com.innowell.core.features.admin.manager.AdminAuthManager;
import com.innowell.core.features.auth.dto.TokenResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/innowell-mapper/auth/admin/token")
public class AdminAuthController {

    @Autowired
    AdminAuthManager adminAuthManager;

    @GetMapping("/login")
    public ResponseEntity<TokenResponseDto> loginIntoAdminClient(@RequestParam("code") String code, @RequestParam("state") String state) {
        TokenResponseDto tokenInfo = adminAuthManager.loginIntoAdminClient(code, state);
        return ResponseEntity.ok(tokenInfo);
    }

}
