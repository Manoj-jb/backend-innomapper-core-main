package com.innowell.core.features.floor.service;

import com.innowell.core.core.models.Floor;

import java.util.List;
import java.util.Optional;

public interface FloorService {
    Floor saveFloor(Floor floor);

    Optional<Floor> findFloorById(Long floorId);

    void softDeleteFloor(Long floorId);

    List<Floor> saveAllFloors(List<Floor> floors);
}
