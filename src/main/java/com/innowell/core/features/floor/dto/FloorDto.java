package com.innowell.core.features.floor.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FloorDto {

    @JsonProperty("floorId")
    private Long floorId;

    @JsonProperty("floorName")
    private String floorName;

    @JsonProperty("floorGeoJson")
    private String floorGeoJson;

    @JsonProperty("isDeleted")
    private Boolean isDeleted;
}
