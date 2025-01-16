package com.innowell.core.features.building.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.innowell.core.features.floor.dto.FloorDto;
import com.innowell.core.features.site.dto.SiteDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildingDto {

    @JsonProperty("buildingId")
    private Long buildingId;

    private SiteDto siteDto;

    @JsonProperty("buildingName")
    private String buildingName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonProperty("buildingGeojson")
    private String buildingGeoJson;

    private String updatedBy;

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @JsonProperty("floors")
    private List<FloorDto> floorDtos;

    @JsonProperty("addedFloors")
    private List<FloorDto> addedFloors;
}
