package com.innowell.core.features.site.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.innowell.core.features.building.dto.BuildingDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NestedSiteDto {
    private Long siteId;
    private String siteName;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @JsonProperty("buildings")
    private List<BuildingDto> nestedBuildingDtos;
}
