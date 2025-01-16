package com.innowell.core.features.building.controller;

import com.innowell.core.core.models.Building;
import com.innowell.core.core.models.CustomUserDetails;
import com.innowell.core.core.models.Floor;
import com.innowell.core.features.building.dto.BuildingDto;
import com.innowell.core.features.building.service.BuildingService;
import com.innowell.core.features.floor.dto.FloorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/innowell-mapper/building")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    //TODO create building

    //TODO edit building


    @GetMapping("/{buildingId}")
    public ResponseEntity<BuildingDto> getBuildingById(@RequestAttribute("user") CustomUserDetails user, @PathVariable Long buildingId) {
        Optional<Building> building = buildingService.findBuildingById(buildingId);
        if (building.isPresent()) {
            BuildingDto buildingDto = convertBuildingToDto1(building.get());
            return new ResponseEntity<>(buildingDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{buildingId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteBuilding(@RequestAttribute("user") CustomUserDetails user, @PathVariable Long buildingId) {
        try {
            buildingService.softDeleteBuilding(buildingId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all/{siteId}")
    public ResponseEntity<List<BuildingDto>> getAllBuildings(@RequestAttribute("user") CustomUserDetails user, @PathVariable Long siteId) {
        List<Building> buildings = buildingService.getBuildingsBySiteId(siteId);
        List<BuildingDto> buildingDtos = buildings.stream()
                .map(this::convertBuildingToDto)
                .collect(Collectors.toList());

        if(buildingDtos.isEmpty()) {
            throw new NoSuchElementException("No buildings found for this site " + siteId);
        }
        return new ResponseEntity<>(buildingDtos, HttpStatus.OK);
    }

    private BuildingDto convertBuildingToDto(Building building) {
        BuildingDto buildingDto = new BuildingDto();
        buildingDto.setBuildingId(building.getBuildingId());
        buildingDto.setBuildingName(building.getBuildingName());
        buildingDto.setBuildingGeoJson(building.getBuildingGeojson());
        buildingDto.setCreatedAt(building.getCreatedAt());
        buildingDto.setUpdatedAt(building.getUpdatedAt());
        return buildingDto;
    }

    private BuildingDto convertBuildingToDto1(Building building) {
        BuildingDto buildingDto = new BuildingDto();
        List<FloorDto> floorDtos = building.getFloors().stream().filter(floor -> !floor.getIsDeleted()).filter(floor -> StringUtils.hasText(floor.getFloorGeojson()))
                .map(this::convertFloorToDto)
                .collect(Collectors.toList());
        buildingDto.setFloorDtos(floorDtos);
        return buildingDto;
    }

    private FloorDto convertFloorToDto(Floor floor) {
        FloorDto floorDto = new FloorDto();
        floorDto.setFloorId(floor.getFloorId());
        floorDto.setFloorName(floor.getFloorName());
        floorDto.setFloorGeoJson(floor.getFloorGeojson());
        return floorDto;
    }
}   
