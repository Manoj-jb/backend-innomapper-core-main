package com.innowell.core.features.casdoor.service;


import com.innowell.core.core.exception.CustomInnowellException;
import com.innowell.core.core.models.Site;
import com.innowell.core.features.casdoor.entity.MappingEntity;
import com.innowell.core.features.casdoor.repository.MappingRepository;
import com.innowell.core.features.site.repository.SiteRepository;
import com.innowell.core.features.site.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrgSiteService {

    @Autowired
    MappingRepository mappingRepository;

    @Autowired
    SiteRepository siteRepository;


    public List<Site> getSitesByOrgId(String orgId) {
        List<Long> siteIds = mappingRepository.findUniqueSitesByOrgId(orgId);
        return siteRepository.findAllById(siteIds);
    }

    public void linkSiteAndOrg(String orgId, Long siteId) {
        MappingEntity mappingEntity = new MappingEntity();
        mappingEntity.setOrgId(orgId);
        mappingEntity.setStatus("ACTIVE");
        Optional<Site> site = siteRepository.findById(siteId);
        if (site.isPresent()) {
            mappingEntity.setSite(site.get());
        } else {
            throw new CustomInnowellException("Site not found");
        }
        mappingRepository.save(mappingEntity);
    }

    public void deleteLink(Site site, String orgId) {
        Optional<MappingEntity> mappingEntity = mappingRepository.findMappingByOrgIdAndSite(orgId, site);
        if (mappingEntity.isPresent()) {
            mappingEntity.get().setStatus("DELETED");
            mappingRepository.save(mappingEntity.get());
        }
    }
}
