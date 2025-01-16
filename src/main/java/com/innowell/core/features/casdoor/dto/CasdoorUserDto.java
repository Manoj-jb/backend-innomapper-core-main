package com.innowell.core.features.casdoor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CasdoorUserDto {
    private String username;
    private List<String> roles;
}
