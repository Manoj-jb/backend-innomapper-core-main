package com.innowell.core.features.floor.service;

import com.innowell.core.core.models.Floor;
import com.innowell.core.features.floor.repository.FloorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class FloorServiceImpl implements FloorService {

    @Autowired
    private FloorRepository floorRepository;

    @Override
    public Floor saveFloor(Floor floor) {
        return floorRepository.save(floor);
    }

    @Override
    public List<Floor> saveAllFloors(List<Floor> floors) {
        return floorRepository.saveAll(floors);  // Batch saving floors
    }

    @Override
    public Optional<Floor> findFloorById(Long floorId) {
        return floorRepository.findByFloorIdAndIsDeletedFalse(floorId);
    }

    @Override
    public void softDeleteFloor(Long floorId) {
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new NoSuchElementException("Floor not found with id " + floorId));

        if (Boolean.TRUE.equals(floor.getIsDeleted())) {
            throw new IllegalStateException("Floor is already deleted with id " + floorId);
        }
        floor.setIsDeleted(true);
        floorRepository.save(floor);
    }
}
