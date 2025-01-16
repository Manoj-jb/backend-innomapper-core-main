package com.innowell.core.features.site.service;

import com.innowell.core.core.models.Site;
import com.innowell.core.features.site.dto.SiteDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SiteService {
    Site saveSite(Site site);

    Optional<Site> getSiteById(Long siteId);

    List<Site> getAllSites();

    void softDeleteSite(Long siteId);

    Page<Site> getPaginatedSitesByIsDeletedFalse(Pageable pageable, String orgId);

    Page<Site> searchSitesByQueryAndIsDeletedFalse(String query, Pageable pageable, String orgId);

    int countBuildingsBySite(Site site);

    SiteDto updateSiteWithBuildingsAndFloors(SiteDto siteDto);
}
