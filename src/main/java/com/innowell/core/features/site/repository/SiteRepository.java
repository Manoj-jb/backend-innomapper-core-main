package com.innowell.core.features.site.repository;

import com.innowell.core.core.models.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    Page<Site> findBySiteNameContainingAndIsDeletedFalse(String name, Pageable pageable);

    Page<Site> findByIsDeletedFalse(Pageable pageable);

    @Query(value = "SELECT s.* FROM sites s " +
            "JOIN org_site_mapping_table osm ON s.site_id = osm.site_id " +
            "WHERE osm.org_id = :orgId AND s.name LIKE %:siteName%",
            nativeQuery = true)
    Page<Site> findSitesByOrgIdAndName(@Param("orgId") String orgId,
                                       @Param("siteName") String siteName,
                                       Pageable pageable);

    @Query(value = "SELECT s.* FROM sites s " +
            "JOIN org_site_mapping_table osm ON s.site_id = osm.site_id " +
            "WHERE osm.org_id = :orgId",
            countQuery = "SELECT COUNT(*) FROM sites s " +
                    "JOIN org_site_mapping_table osm ON s.site_id = osm.site_id " +
                    "WHERE osm.org_id = :orgId",
            nativeQuery = true)
    Page<Site> findAllSitesByOrgId(@Param("orgId") String orgId, Pageable pageable);


    Optional<Site> findBySiteIdAndIsDeletedFalse(Long siteId);
}
