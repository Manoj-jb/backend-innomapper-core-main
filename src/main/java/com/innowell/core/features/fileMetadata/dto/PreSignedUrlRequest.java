package com.innowell.core.features.fileMetadata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreSignedUrlRequest {
    private String fileName;
    private String fileType;
    private String source;
    private String sourceId;
}
