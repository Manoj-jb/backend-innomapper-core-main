package com.innowell.core.features.admin.service;

import com.innowell.core.features.admin.dto.UpdateClientDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UpdateClientService {


    @Value("${api.key}")
    private String clientApiKey;

    private static final String API_URL = "https://ajl.targetsnetzero.com/iot/saveClientDetails";
    
    public ResponseEntity<String> saveClient(UpdateClientDto client) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("x-api-key", clientApiKey);
        
        HttpEntity<UpdateClientDto> requestEntity = new HttpEntity<>(client, headers);
        
        return restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, String.class);
    }
}