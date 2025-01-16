package com.innowell.core.features.fileMetadata.service;

import com.innowell.core.features.fileMetadata.dto.PreSignedUrlResponse;

import java.util.Map;

public interface FileMetadataService {
    PreSignedUrlResponse getPreSignedUrl(String fileName, String fileType, String source, String sourceId);

    Boolean acknowledgeClientUpload(String fileId, Long fileSize);

    String getUrlWithExpiryAndId(String id, Integer days);

    Map<String, String> getUrlWithExpiryForMultipleFiles(String ids, Integer days);
}
