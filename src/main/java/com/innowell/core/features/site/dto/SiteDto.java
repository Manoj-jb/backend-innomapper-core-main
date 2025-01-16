package com.innowell.core.features.site.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.innowell.core.features.building.dto.BuildingDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteDto {
    @JsonProperty("siteId")
    private Long siteId;
    @JsonProperty("siteName")
    private String siteName;
    @JsonProperty("address")
    private String address;
    @JsonProperty("latitude")
    private BigDecimal latitude;
    @JsonProperty("longitude")
    private BigDecimal longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    private int numberOfbuildings;

    @JsonProperty("buildings")
    private List<BuildingDto> buildingDtos = new ArrayList<>();

    @JsonProperty("addedBuildings")
    private List<BuildingDto> addedBuildings;
}