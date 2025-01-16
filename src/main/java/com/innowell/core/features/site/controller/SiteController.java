package com.innowell.core.features.site.controller;

import com.innowell.core.core.exception.CustomInnowellException;
import com.innowell.core.core.models.Building;
import com.innowell.core.core.models.CustomUserDetails;
import com.innowell.core.core.models.Floor;
import com.innowell.core.core.models.Site;
import com.innowell.core.features.building.dto.BuildingDto;
import com.innowell.core.features.casdoor.service.OrgSiteService;
import com.innowell.core.features.floor.dto.FloorDto;
import com.innowell.core.features.site.dto.NestedSiteDto;
import com.innowell.core.features.site.dto.SiteDto;
import com.innowell.core.features.site.dto.SitePagination;
import com.innowell.core.features.site.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/innowell-mapper/site")
public class SiteController {

    @Autowired
    private SiteService siteService;

    @Autowired
    private OrgSiteService orgSiteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> createSite(@RequestAttribute("user") CustomUserDetails user, @RequestBody SiteDto siteDto) {
        if (Objects.isNull(siteDto)) {
            throw new CustomInnowellException("SiteDto cannot be null");
        }
        Site site = convertSiteToEntity(siteDto);
        if (Objects.isNull(site)) {
            throw new CustomInnowellException("Failed to convert SiteDto to Site");
        }
        Site saveSite = siteService.saveSite(site);
        if (Objects.isNull(saveSite)) {
            throw new CustomInnowellException("Failed to save Site");
        }
        else {
            orgSiteService.linkSiteAndOrg(user.getOrgId(), saveSite.getSiteId());
        }
        Map<String, Long> reponse = new HashMap<>();
        reponse.put("siteId", saveSite.getSiteId());
        return new ResponseEntity<Map<String, Long>>(reponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NestedSiteDto> getSiteById(@RequestAttribute("user") CustomUserDetails user,@PathVariable Long id) {
        Optional<Site> site = siteService.getSiteById(id);
        if (site.isPresent()) {
            NestedSiteDto siteDto = nestedDtoForGetSiteAPI(site.get());
            return new ResponseEntity<>(siteDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<SiteDto>> getAllSites(@RequestAttribute("user") CustomUserDetails user) {
        List<Site> sites = new ArrayList<>();

        if(user.getOrgId() != null && (user.getOrgId().equals("admin") || user.getOrgId().equals("innowell") || user.getOrgId().equals("built-in") || user.getOrgId().equals("vidyayatan"))) {
                sites = siteService.getAllSites();
        }
        if(sites.isEmpty()) sites = orgSiteService.getSitesByOrgId(user.getOrgId());

        List<SiteDto> respDtos = sites.stream()
                .filter(site -> !site.getIsDeleted())
                .map(this::convertSiteToDto).toList();

        return new ResponseEntity<>(respDtos, HttpStatus.OK);
    }

    @GetMapping("/paginated")
    public ResponseEntity<SitePagination<SiteDto>> getPaginatedSites(
            @RequestAttribute("user") CustomUserDetails user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String query) {


        if (page < 1) {
            throw new CustomInnowellException("Page index should be greater than or equal to 1.");
        }

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Site> sitePage;
        if (query != null && !query.trim().isEmpty()) {
            sitePage = siteService.searchSitesByQueryAndIsDeletedFalse(query, pageable, user.getOrgId());
        } else {
            sitePage = siteService.getPaginatedSitesByIsDeletedFalse(pageable, user.getOrgId());
        }

        List<SiteDto> siteDtos = sitePage.getContent().stream()
                .map(this::convertSiteToDto)
                .toList();

        SitePagination<SiteDto> response = new SitePagination<>();
        response.setContent(siteDtos);
        response.setTotalPages(sitePage.getTotalPages());
        response.setTotalElements(sitePage.getTotalElements());
        response.setPageNo(sitePage.getNumber() + 1);
        response.setPageSize(sitePage.getSize());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> updateSite(@RequestAttribute("user") CustomUserDetails user,@RequestBody SiteDto siteDto) {
        if (siteDto == null || siteDto.getSiteId() == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            SiteDto updatedSite = siteService.updateSiteWithBuildingsAndFloors(siteDto);
            Map<String, String> response = new HashMap<>();
            response.put("siteId", String.valueOf(updatedSite.getSiteId()));
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException ex) {
            throw new CustomInnowellException("Site not found for the given ID" + siteDto.getSiteId());
        }
    }

    @DeleteMapping("/{siteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteSite(@RequestAttribute("user") CustomUserDetails user,@PathVariable Long siteId) {
        try {
            Site site = siteService.getSiteById(siteId).orElseThrow(() -> new NoSuchElementException("Site not found with id " + siteId));
            siteService.softDeleteSite(siteId);
            orgSiteService.deleteLink(site, user.getOrgId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (NoSuchElementException ex) {
            throw new CustomInnowellException("Site id is not found " + siteId);
            // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Entity Converter

    private Site convertSiteToEntity(SiteDto siteDto) {
        if (Objects.isNull(siteDto)) {
            throw new CustomInnowellException("SiteDto cannot be null");
        }
        Site site = new Site();
        site.setSiteId(siteDto.getSiteId());
        site.setSiteName(siteDto.getSiteName());
        site.setAddress(siteDto.getAddress());
        site.setLatitude(siteDto.getLatitude());
        site.setLongitude(siteDto.getLongitude());
        site.setUpdatedBy(siteDto.getUpdatedBy());
        site.setIsDeleted(siteDto.getIsDeleted());

        List<Building> buildings = siteDto.getBuildingDtos().stream()
                .map(buildingDto -> convertBuildingToEntity(buildingDto, site))
                .collect(Collectors.toList());
        site.setBuildings(buildings);
        return site;
    }

    private Building convertBuildingToEntity(BuildingDto buildingDto, Site site) {
        Building building = new Building();
        building.setBuildingId(buildingDto.getBuildingId());
        building.setBuildingName(buildingDto.getBuildingName());
        building.setSite(site);

        List<Floor> floors = buildingDto.getFloorDtos().stream()
                .map(this::convertFloorEntity)
                .collect((Collectors.toList()));
        building.setFloors(floors);
        return building;
    }

    private Floor convertFloorEntity(FloorDto floorDto) {
        Floor floor = new Floor();
        floor.setFloorName(floorDto.getFloorName());
        floor.setFloorGeojson(floorDto.getFloorGeoJson());
        return floor;
    }


    //DTO Converter

    private SiteDto convertSiteToDto(Site site) {
        if (Objects.isNull(site)) {
            throw new CustomInnowellException("Site cannot be null");
        }
        SiteDto siteDto = new SiteDto();
        siteDto.setSiteId(site.getSiteId());
        siteDto.setSiteName(site.getSiteName());
        siteDto.setAddress(site.getAddress());
        siteDto.setLatitude(site.getLatitude());
        siteDto.setLongitude(site.getLongitude());
        siteDto.setCreatedAt(site.getCreatedAt());
        siteDto.setUpdatedAt(site.getUpdatedAt());
        siteDto.setUpdatedBy(site.getUpdatedBy());
        siteDto.setIsDeleted(site.getIsDeleted());
        int countBuildings = siteService.countBuildingsBySite(site);
        siteDto.setNumberOfbuildings(countBuildings);
        return siteDto;
    }

    private NestedSiteDto nestedDtoForGetSiteAPI(Site site) {
        if (Objects.isNull(site)) {
            throw new CustomInnowellException("Site cannot be null");
        }
        NestedSiteDto nestedSiteDto = new NestedSiteDto();
        nestedSiteDto.setSiteId(site.getSiteId());
        nestedSiteDto.setSiteName(site.getSiteName());
        nestedSiteDto.setAddress(site.getAddress());
        nestedSiteDto.setLatitude(site.getLatitude());
        nestedSiteDto.setLongitude(site.getLongitude());

        List<BuildingDto> buildingDtos = site.getBuildings().stream()
                .filter(building -> Boolean.FALSE.equals(building.getIsDeleted()))
                .map(this::convertBuildingToDto)
                .collect(Collectors.toList());
        nestedSiteDto.setNestedBuildingDtos(buildingDtos);

        return nestedSiteDto;
    }

    private BuildingDto convertBuildingToDto(Building building) {
        BuildingDto buildingDto = new BuildingDto();
        buildingDto.setBuildingId(building.getBuildingId());
        buildingDto.setBuildingName(building.getBuildingName());
        List<FloorDto> floorDtos = building.getFloors().stream()
                .filter(floor -> Boolean.FALSE.equals(floor.getIsDeleted()))
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
