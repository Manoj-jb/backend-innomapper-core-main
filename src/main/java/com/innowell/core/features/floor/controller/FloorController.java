package com.innowell.core.features.floor.controller;

import com.innowell.core.core.exception.CustomInnowellException;
import com.innowell.core.core.models.CustomUserDetails;
import com.innowell.core.core.models.Floor;
import com.innowell.core.features.floor.dto.FloorDto;
import com.innowell.core.features.floor.service.FloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/innowell-mapper/floor")
public class FloorController {

    @Autowired
    private FloorService floorService;


    //TODO getAllFloor

    //TODO saveFloor

    //TODO updateFloor

    @GetMapping("/{floorId}")
    public ResponseEntity<FloorDto> getFloorById(@RequestAttribute("user") CustomUserDetails user, @PathVariable Long floorId) {
        Optional<Floor> floor = floorService.findFloorById(floorId);
        if (floor.isPresent()) {
            FloorDto floorDto = convertFloorToDto(floor.get());
            return new ResponseEntity<>(floorDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFloor(@RequestAttribute("user") CustomUserDetails user, @PathVariable Long id) {
        try {
            floorService.softDeleteFloor(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private FloorDto convertFloorToDto(Floor floor) {
        if (Objects.isNull(floor)) {
            throw new CustomInnowellException("Floor cannot be null");
        }
        FloorDto floorDto = new FloorDto();
        floorDto.setFloorId(floor.getFloorId());
        floorDto.setFloorName(floor.getFloorName());
        floorDto.setFloorGeoJson(floor.getFloorGeojson());
        return floorDto;
    }
}
