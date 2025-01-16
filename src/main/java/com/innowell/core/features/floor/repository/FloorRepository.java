package com.innowell.core.features.floor.repository;

import com.innowell.core.core.models.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {
    Optional<Floor> findByFloorIdAndIsDeletedFalse(Long floorId);
}
