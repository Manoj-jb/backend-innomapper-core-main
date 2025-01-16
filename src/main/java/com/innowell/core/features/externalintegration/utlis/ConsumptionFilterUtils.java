package com.innowell.core.features.externalintegration.utlis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ConsumptionFilterUtils {

    //case1. when site_id is provided
    public static JsonNode filterResponseWhenSiteIdIsProvided(JsonNode consumptionResponse) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode filteredNode = mapper.createObjectNode();

    
        return filteredNode;
    }

    //case2. when site_Id and building_id is provided 
    public static JsonNode filterResponseWhenSiteIdAndBuildingIdIsProvided(JsonNode consumptionResponse, String buildingIdFromRequest) {
        ObjectMapper mapper = new ObjectMapper();
        
        for (JsonNode building : consumptionResponse) {
            String buildingIdFromResponse = building.has("building_id") ? building.get("building_id").asText() : null;
    
            if (buildingIdFromResponse != null && buildingIdFromResponse.equals(buildingIdFromRequest)) {

                return building;
            }
        }
        
        return mapper.createObjectNode();  
    }
    
    //case3. when site_Id, building_id and floor_Id is provided 
    public static JsonNode filterResponseWhenSiteIdAndBuildingIdAndFloorIdIsProvided(JsonNode consumptionResponse, String floorIdFromRequest) {
        ObjectMapper mapper = new ObjectMapper();
        
        for (JsonNode floor : consumptionResponse) {
            String floorIdFromResponse = floor.has("floor_id") ? floor.get("floor_id").asText() : null;
    
            if (floorIdFromResponse != null && floorIdFromResponse.equals(floorIdFromRequest)) {
                return floor;
            }
        }
        
        return mapper.createObjectNode();
    }
    
    //case4. when site_Id, building_id, floor_Id and zone_id is provided 
    public static JsonNode filterResponseWhenSiteIBuildingIdFloorIdZoneIdIsProvided(
        JsonNode consumptionResponse,
        String siteIdFromRequest,
        String buildingIdFromRequest,
        String floorIdFromRequest,
        String zoneIdFromRequest) {

        if (consumptionResponse != null && consumptionResponse.isArray()) {
            for (JsonNode zoneData : consumptionResponse) {
                if (zoneData.has("zone_id") && zoneData.get("zone_id").asText().equals(zoneIdFromRequest)) {
                    return zoneData;
                }
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode errorResponse = mapper.createObjectNode();
        errorResponse.put("error", "No matching zone found for the provided zone_id");
        return errorResponse;
    }

    // case5. when site_Id, building_id, floor_Id, zone_id and space_Id is provided 
    public static JsonNode filterResponseWhenSiteIBuildingIdFloorIdZoneIdSpaceIdIsProvided(
        JsonNode consumptionResponse,
        String siteIdFromRequest,
        String buildingIdFromRequest,
        String floorIdFromRequest,
        String zoneIdFromRequest,
        String spaceIdFromRequest) {

        if (consumptionResponse != null && consumptionResponse.isArray()) {
            for (JsonNode zoneData : consumptionResponse) {
                if (zoneData.has("zone_id") && zoneData.get("zone_id").asText().equals(zoneIdFromRequest)) {

                    if (zoneData.has("space_info") && zoneData.get("space_info").isArray()) {
                        for (JsonNode spaceData : zoneData.get("space_info")) {
                            if (spaceData.has("space_id") && spaceData.get("space_id").asText().equals(spaceIdFromRequest)) {

                                return spaceData;
                            }
                        }
                    }
                }
            }
        }
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode errorResponse = mapper.createObjectNode();
        errorResponse.put("error", "No matching space found for the provided space_id");
        return errorResponse;
    }

}
