package com.innowell.core.features.building.repository;

import com.innowell.core.core.models.Building;
import com.innowell.core.core.models.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    int countBySiteAndIsDeletedFalse(Site site);

    Optional<Building> findByBuildingIdAndIsDeletedFalse(Long buildingId);

    @Query("SELECT b FROM Building b WHERE b.site.id = :siteId AND b.isDeleted = false")
    List<Building> findBySiteIdAndIsDeletedFalse(@Param("siteId") Long siteId);
}
