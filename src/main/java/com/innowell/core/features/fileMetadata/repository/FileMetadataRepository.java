package com.innowell.core.features.fileMetadata.repository;

import com.innowell.core.core.models.FileMatadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMatadata, String> {
    Optional<FileMatadata> findTopBySourceAndSourceId(String source, String sourceId);
}
