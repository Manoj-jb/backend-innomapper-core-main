package com.innowell.core.features.casdoor.repository;

import com.innowell.core.core.models.Site;
import com.innowell.core.features.casdoor.entity.MappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MappingRepository extends JpaRepository<MappingEntity, String> {

    // Native query to get all unique site IDs for a given organization ID
    @Query(value = "SELECT DISTINCT m.site_id FROM org_site_mapping_table m WHERE m.org_id = :orgId and m.status <> 'DELETED' ", nativeQuery = true)
    List<Long> findUniqueSitesByOrgId(@Param("orgId") String orgId);

    Optional<MappingEntity> findMappingByOrgIdAndSite(String orgId, Site site);
}