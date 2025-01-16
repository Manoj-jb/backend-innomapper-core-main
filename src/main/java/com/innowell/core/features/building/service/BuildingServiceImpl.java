package com.innowell.core.features.building.service;

import com.innowell.core.core.models.Building;
import com.innowell.core.core.models.Floor;
import com.innowell.core.core.models.Site;
import com.innowell.core.features.building.repository.BuildingRepository;
import com.innowell.core.features.site.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@Lazy
public class BuildingServiceImpl implements BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private SiteService siteService;

    @Override
    public Building saveBuilding(Building building) {
        return buildingRepository.save(building);
    }

    @Override
    public List<Building> saveAllBuildings(List<Building> buildings) {
        return buildingRepository.saveAll(buildings);  // Batch saving buildings
    }

    @Override
    public Site findSiteById(Long siteId) {
        return siteService.getSiteById(siteId).orElseThrow(() -> new NoSuchElementException("Site not found with id: " + siteId));
    }

    @Override
    public List<Building> getBuildingsBySiteId(Long siteId) {
        List<Building> listOfAllbuildings = buildingRepository.findBySiteIdAndIsDeletedFalse(siteId);
        
        for(Building building : listOfAllbuildings) {
            // Filter floors where is_deleted = false and find the smallest floor by ID
            Optional<Floor>samllestFloor = building.getFloors().stream()
                                            .filter(floor -> Boolean.FALSE.equals(floor.getIsDeleted()))
                                            .min((f1,f2) -> Long.compare(f1.getFloorId(), f2.getFloorId()));
            if(samllestFloor.isPresent()) {
                // If a non-deleted floor is found, set its geojson to the building
                building.setBuildingGeojson(samllestFloor.get().getFloorGeojson());
            }
            else {
                // If no non-deleted floor is found, set the buildingGeojson to an empty string
                building.setBuildingGeojson("");
            }
        }
        return listOfAllbuildings;
    }

    @Override
    public Optional<Building> findBuildingById(Long buildingId) {
        return buildingRepository.findByBuildingIdAndIsDeletedFalse(buildingId);
    }

    public void softDeleteBuilding(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new NoSuchElementException("Site not found with id " + buildingId));
        if (Boolean.TRUE.equals(building.getIsDeleted())) {
            throw new IllegalStateException("Building is already deleted with id " + buildingId);
        }
        building.setIsDeleted(true);
        buildingRepository.save(building);
    }
}
