package com.innowell.core.features.fileMetadata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreSignedUrlResponse {
    private String id;
    private String url;
}
