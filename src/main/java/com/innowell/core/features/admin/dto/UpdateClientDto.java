package com.innowell.core.features.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateClientDto {
    @JsonProperty("client_id")
    private String clientId;
    
    @JsonProperty("client_status")
    private String clientStatus;
    
    @JsonProperty("client_name")
    private String clientName;
    
    @JsonProperty("short_name")
    private String shortName;
    
    @JsonProperty("client_url")
    private String clientUrl;
    
    @JsonProperty("admin_email")
    private String adminEmail;
    
    @JsonProperty("admin_name")
    private String adminName;

}