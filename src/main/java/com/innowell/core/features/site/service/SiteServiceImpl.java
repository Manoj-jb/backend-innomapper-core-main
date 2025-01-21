package com.innowell.core.features.site.service;

import com.innowell.core.core.models.Building;
import com.innowell.core.core.models.Floor;
import com.innowell.core.core.models.Site;
import com.innowell.core.features.building.dto.BuildingDto;
import com.innowell.core.features.building.repository.BuildingRepository;
import com.innowell.core.features.building.service.BuildingService;
import com.innowell.core.features.floor.dto.FloorDto;
import com.innowell.core.features.floor.service.FloorService;
import com.innowell.core.features.site.dto.SiteDto;
import com.innowell.core.features.site.repository.SiteRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;
    private final BuildingRepository buildingRepository;
    private final BuildingService buildingService;
    private final FloorService floorService;

    public SiteServiceImpl(SiteRepository siteRepository,
                           BuildingRepository buildingRepository,
                           @Lazy BuildingService buildingService,
                           FloorService floorService) {
        this.siteRepository = siteRepository;
        this.buildingRepository = buildingRepository;
        this.buildingService = buildingService;
        this.floorService = floorService;
    }

    @Override
    public Site saveSite(Site site) {
        if(Objects.isNull(site.getIsDeleted())) {
            site.setIsDeleted(false);
        }
        Site savSite = siteRepository.save(site);
        site.getBuildings().forEach(building -> {
            building.setSite(savSite);
            Building saveBuilding = buildingService.saveBuilding(building);
            building.getFloors().forEach(floor -> {
                floor.setBuilding(saveBuilding);
                floorService.saveFloor(floor);
            });
        });
        return savSite;
    }

    @Override
    public Optional<Site> getSiteById(Long siteId) {
        return siteRepository.findBySiteIdAndIsDeletedFalse(siteId);
    }

    @Override
    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    @Override
    public void softDeleteSite(Long siteId) {
        Site site = siteRepository.findBySiteIdAndIsDeletedFalse(siteId)
                .orElseThrow(() -> new NoSuchElementException("Site not found with id " + siteId));
        
        List<Building>listOfAllBuildings = buildingService.getBuildingsBySiteId(siteId);
        
        if(listOfAllBuildings != null && !listOfAllBuildings.isEmpty()) {

            listOfAllBuildings.forEach(building -> {
                List<Floor> listOfAllFloors = building.getFloors().stream()
                    .filter(floor -> Boolean.FALSE.equals(floor.getIsDeleted()))
                    .collect(Collectors.toList());
                
                if(!listOfAllFloors.isEmpty()) {
                    listOfAllFloors.forEach(floor -> floor.setIsDeleted(true));
                    floorService.saveAllFloors(listOfAllFloors);
                }
                
                building.setIsDeleted(true);
            });
            buildingService.saveAllBuildings(listOfAllBuildings);
        }
        site.setIsDeleted(true);
        siteRepository.save(site);
    }

    @Override
    public Page<Site> getPaginatedSitesByIsDeletedFalse(Pageable pageable, String orgId) {
        Page<Site> sites = null;
        if(orgId != null && (orgId.equals("admin") || orgId.equals("innowell") || orgId.equals("built-in") || orgId.equals("vidyayatan"))) {
            sites = siteRepository.findByIsDeletedFalse(pageable);
        }
        if (sites == null || sites.isEmpty()) sites = siteRepository.findAllSitesByOrgId(orgId, pageable);
        return sites;
    }

    @Override
    public Page<Site> searchSitesByQueryAndIsDeletedFalse(String query, Pageable pageable, String orgId) {
        Page<Site> sites = null;
        if(orgId != null && (orgId.equals("admin") || orgId.equals("innowell") || orgId.equals("built-in") || orgId.equals("vidyayatan"))) {
            sites = siteRepository.findBySiteNameContainingAndIsDeletedFalse(query, pageable);
        }
        if (sites == null || sites.isEmpty()) sites = siteRepository.findSitesByOrgIdAndName(orgId, query, pageable);
        return sites;
    }

    @Override
    public int countBuildingsBySite(Site site) {
        return buildingRepository.countBySiteAndIsDeletedFalse(site);
    }

    @Override
    public SiteDto updateSiteWithBuildingsAndFloors(SiteDto siteDto) {
        Long siteId = siteDto.getSiteId();
        Site existingSite = siteRepository.findBySiteIdAndIsDeletedFalse(siteId)
                .orElseThrow(() -> new NoSuchElementException("Site not found with ID: " + siteId));

        updateSiteDetails(siteDto, existingSite);

        // Handle "addedBuildings" keyword for new buildings
        if (siteDto.getAddedBuildings() != null) {
            for (BuildingDto buildingDto : siteDto.getAddedBuildings()) {
                saveNewBuilding(buildingDto, existingSite);
            }
        }

        // Handle "buildingDtos" keyword for existing buildings
        if (siteDto.getBuildingDtos() != null) {
            for (BuildingDto buildingDto : siteDto.getBuildingDtos()) {
                updateExistingBuilding(buildingDto, existingSite);
            }
        }
        Site updatedSite = siteRepository.save(existingSite);
        return convertSiteToDto(updatedSite);
    }

    private void updateSiteDetails(SiteDto siteDto, Site existingSite) {
        if (Objects.nonNull(siteDto.getSiteName())) {
            existingSite.setSiteName(siteDto.getSiteName());
        }
        if (Objects.nonNull(siteDto.getAddress())) {
            existingSite.setAddress(siteDto.getAddress());
        }
        if (Objects.nonNull(siteDto.getLatitude())) {
            existingSite.setLatitude(siteDto.getLatitude());
        }
        if (Objects.nonNull(siteDto.getLongitude())) {
            existingSite.setLongitude(siteDto.getLongitude());
        }
    }

    private void saveNewBuilding(BuildingDto buildingDto, Site existingSite) {
        Building newBuilding = new Building();
        newBuilding.setBuildingName(buildingDto.getBuildingName());
        newBuilding.setIsDeleted(false);
        newBuilding.setSite(existingSite);
        buildingService.saveBuilding(newBuilding);
        saveFloorsForBuilding(buildingDto.getAddedFloors(), newBuilding);
    }

    private void saveFloorsForBuilding(List<FloorDto> floorDtos, Building building) {
        if (Objects.nonNull(floorDtos)) {
            for (FloorDto floorDto : floorDtos) {
                Floor newFloor = new Floor();
                newFloor.setFloorName(floorDto.getFloorName());
                newFloor.setFloorGeojson(floorDto.getFloorGeoJson());
                newFloor.setBuilding(building);
                floorService.saveFloor(newFloor);
            }
        }
    }

    private void updateExistingBuilding(BuildingDto buildingDto, Site existingSite) {
        Building existingBuilding = buildingService.findBuildingById(buildingDto.getBuildingId())
                .orElseThrow(() -> new NoSuchElementException("Building not found with ID: " + buildingDto.getBuildingId()));

        if (!existingBuilding.getIsDeleted()) {

            existingBuilding.setSite(existingSite);

            if (Objects.nonNull(buildingDto.getBuildingName())) {
                existingBuilding.setBuildingName(buildingDto.getBuildingName());
            }
            if (Objects.nonNull(buildingDto.getIsDeleted())) {
                existingBuilding.setIsDeleted(buildingDto.getIsDeleted());
            }

            saveFloorsForBuilding(buildingDto.getAddedFloors(), existingBuilding);

            // Handle "floors" keyword for existing floors
            if (buildingDto.getFloorDtos() != null) {
                for (FloorDto floorDto : buildingDto.getFloorDtos()) {
                    updateExistingFloor(floorDto);
                }
            }
            buildingService.saveBuilding(existingBuilding);
        }
    }

    private void updateExistingFloor(FloorDto floorDto) {
        Floor existingFloor = floorService.findFloorById(floorDto.getFloorId())
                .orElseThrow(() -> new NoSuchElementException("Floor not found with ID: " + floorDto.getFloorId()));

        if (!existingFloor.getIsDeleted()) {
            if (Objects.nonNull(floorDto.getFloorName())) {
                existingFloor.setFloorName(floorDto.getFloorName());
            }
            if (Objects.nonNull(floorDto.getFloorGeoJson())) {
                existingFloor.setFloorGeojson(floorDto.getFloorGeoJson());
            }
            if (Objects.nonNull(floorDto.getIsDeleted())) {
                existingFloor.setIsDeleted(floorDto.getIsDeleted());
            }
        }
    }

    private SiteDto convertSiteToDto(Site site) {
        SiteDto siteDto = new SiteDto();
        siteDto.setSiteId(site.getSiteId());
        siteDto.setSiteName(site.getSiteName());
        siteDto.setAddress(site.getAddress());
        siteDto.setLatitude(site.getLatitude());
        siteDto.setLongitude(site.getLongitude());
        return siteDto;
    }
}
