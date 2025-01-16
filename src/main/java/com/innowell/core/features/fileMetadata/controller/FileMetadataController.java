package com.innowell.core.features.fileMetadata.controller;

import com.innowell.core.core.exception.CustomInnowellException;
import com.innowell.core.features.fileMetadata.dto.PreSignedUrlRequest;
import com.innowell.core.features.fileMetadata.dto.PreSignedUrlResponse;
import com.innowell.core.features.fileMetadata.service.FileMetadataService;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/innowell-mapper/file-metadata")
public class FileMetadataController {

    @Autowired
    private FileMetadataService fileMetadataService;

    @PostMapping("/get-signed-url")
    public ResponseEntity<PreSignedUrlResponse> uploadfile(@RequestBody PreSignedUrlRequest preSignedUrlRequest) {
        PreSignedUrlResponse url = fileMetadataService.getPreSignedUrl(preSignedUrlRequest.getFileName(), preSignedUrlRequest.getFileType(), preSignedUrlRequest.getSource(), preSignedUrlRequest.getSourceId());
        return ResponseEntity.ok(url);
    }

    @PostMapping("/acknowledge")
    public ResponseEntity<Boolean> acknowledgeUpload(@RequestParam String fileId, @RequestParam String fileSize) {
        return ResponseEntity.ok(fileMetadataService.acknowledgeClientUpload(fileId, Long.valueOf(fileSize)));
    }

    @GetMapping("/get-public-url")
    public ResponseEntity<String> getFile(@RequestParam String fileId, @RequestParam Integer expiryDays) {
        String url = fileMetadataService.getUrlWithExpiryAndId(fileId, expiryDays);
        if(Objects.isNull(url)) {
            throw new CustomInnowellException("Failed to generate the public url");
        }
        return ResponseEntity.ok(url);
    }

    @GetMapping("/get-public-urls")
    public ResponseEntity<Map<String, String>> getFiles(@RequestParam String fileIds, @RequestParam Integer expiryDays) {
        Map<String, String> urls = fileMetadataService.getUrlWithExpiryForMultipleFiles(fileIds, expiryDays);
        if(Objects.isNull(urls)) {
            throw new CustomInnowellException("Failed to generate the public url");
        }
        return ResponseEntity.ok(urls);
    }
}
