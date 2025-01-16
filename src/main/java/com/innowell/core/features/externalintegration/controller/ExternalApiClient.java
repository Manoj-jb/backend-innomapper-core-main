package com.innowell.core.features.externalintegration.controller;

import java.util.HashMap;
import java.util.Map;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innowell.core.core.exception.CustomInnowellException;
import com.innowell.core.core.models.CustomUserDetails;
import com.innowell.core.features.externalintegration.utlis.ConsumptionFilterUtils;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/innowell-mapper/external-api-client")
public class ExternalApiClient {

    @Autowired
    private WebClient webClient;

    @GetMapping("/getConsumption")
    public Mono<ResponseEntity<JsonNode>> getConsumption(
        @RequestAttribute("user") CustomUserDetails user,
        @RequestParam(required = false) String site_id,
        @RequestParam(required = false) String building_id,
        @RequestParam(required = false) String floor_id,
        @RequestParam(required = false) String zone_id,
        @RequestParam(required = false) String space_id
    ) {
        
        Map<String, Object> requestData = new HashMap<>();
        if (Objects.nonNull(site_id) && Objects.isNull(building_id) && Objects.isNull(floor_id) && Objects.isNull(zone_id) && Objects.isNull(space_id)) {
            requestData.put("site_id", site_id);
        } 
        else if (Objects.nonNull(site_id) && Objects.nonNull(building_id) && Objects.isNull(floor_id) && Objects.isNull(zone_id) && Objects.isNull(space_id)) {
            requestData.put("site_id", site_id);
            requestData.put("building_id", building_id);
        } 
        else if (Objects.nonNull(site_id) && Objects.nonNull(building_id) && Objects.nonNull(floor_id) && Objects.isNull(zone_id) && Objects.isNull(space_id)) {
            requestData.put("site_id", site_id);
            requestData.put("building_id", building_id);
            requestData.put("floor_id", floor_id);
        } 
        else if (Objects.nonNull(site_id) && Objects.nonNull(building_id) && Objects.nonNull(floor_id) && Objects.nonNull(zone_id) && Objects.isNull(space_id)) {
            requestData.put("site_id", site_id);
            requestData.put("building_id", building_id);
            requestData.put("floor_id", floor_id);
            requestData.put("zone_id", zone_id);
        } 
        else if (Objects.nonNull(site_id) && Objects.nonNull(building_id) && Objects.nonNull(floor_id) && Objects.nonNull(zone_id) && Objects.nonNull(space_id)) {
            requestData.put("site_id", site_id);
            requestData.put("building_id", building_id);
            requestData.put("floor_id", floor_id);
            requestData.put("zone_id", zone_id);
            requestData.put("space_Id", space_id);
        }
        else {
            throw new CustomInnowellException("Provide valid details");
        }

        try {
            return webClient.post()
                .uri("/iot/getConsumption")
                .bodyValue(requestData)
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        Mono.error(new CustomInnowellException("Failed to get consumption API"))
                )
                .bodyToMono(JsonNode.class)
                .map(consumptionResponse -> {
                    JsonNode filteredResponse = null;
                    if(site_id != null && building_id == null && floor_id == null && floor_id == null && zone_id == null && space_id == null) {
                        filteredResponse = consumptionResponse;
                    }
                    else if(site_id != null && building_id != null && floor_id == null && zone_id == null && space_id == null) {
                        filteredResponse = ConsumptionFilterUtils.filterResponseWhenSiteIdAndBuildingIdIsProvided(consumptionResponse,building_id);
                    }
                    else if(site_id != null && building_id != null && floor_id != null && zone_id == null && space_id == null) {
                        filteredResponse = ConsumptionFilterUtils.filterResponseWhenSiteIdAndBuildingIdAndFloorIdIsProvided(consumptionResponse,floor_id);
                    }
                    else if(site_id != null && building_id != null && floor_id != null && zone_id != null && space_id == null) {
                        filteredResponse = ConsumptionFilterUtils.filterResponseWhenSiteIBuildingIdFloorIdZoneIdIsProvided(consumptionResponse,site_id,building_id,floor_id,zone_id);
                    }
                    else if(site_id != null && building_id != null && floor_id != null && zone_id != null && space_id != null) {
                        filteredResponse = ConsumptionFilterUtils.filterResponseWhenSiteIBuildingIdFloorIdZoneIdSpaceIdIsProvided(consumptionResponse,site_id,building_id,floor_id,zone_id,space_id);
                    }
                    return ResponseEntity.ok(filteredResponse);
                })
                .onErrorResume(ex -> {
                    JsonNode errorResponse = createErrorResponse();
                    return Mono.just(ResponseEntity.status(500).body(errorResponse));
                });
        } 
        catch (Exception ex) {
            throw new CustomInnowellException("Error calling external API: " + ex.getMessage());
    
        }
    }
    
    // Create a custom error response, e.g.:
    private JsonNode createErrorResponse() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("error", "Error calling external API");
        return errorNode;
    }
}
