package com.innowell.core.features.building.service;

import com.innowell.core.core.models.Building;
import com.innowell.core.core.models.Site;

import java.util.List;
import java.util.Optional;

public interface BuildingService {
    Site findSiteById(Long siteId);

    Building saveBuilding(Building building);

    List<Building> getBuildingsBySiteId(Long siteId);

    Optional<Building> findBuildingById(Long buildingId);

    void softDeleteBuilding(Long buildingId);

    List<Building> saveAllBuildings(List<Building> buildings);
}
