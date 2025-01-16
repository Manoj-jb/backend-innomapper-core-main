package com.innowell.core.features.casdoor.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innowell.core.core.exception.CustomInnowellException;
import com.innowell.core.core.models.CustomUserDetails;
import com.innowell.core.features.casdoor.dto.CasdoorUserDto;


@RestController
@RequestMapping("/innowell-mapper/casdoor-user")
public class CasdoorUserController {

    @GetMapping
    public ResponseEntity<CasdoorUserDto> getCasdoorUser(@RequestAttribute("user") CustomUserDetails user) {
        if (Objects.isNull(user)) {
            throw new CustomInnowellException("Authentication failed please provide a valid token");
        }

        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        CasdoorUserDto casdoorUserDto = new CasdoorUserDto(user.getUsername(), roles);
        return ResponseEntity.ok(casdoorUserDto);
    }
}
