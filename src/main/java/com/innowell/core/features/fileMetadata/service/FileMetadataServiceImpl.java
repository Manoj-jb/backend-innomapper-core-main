package com.innowell.core.features.fileMetadata.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.innowell.core.core.exception.CustomInnowellException;
import com.innowell.core.core.models.FileMatadata;
import com.innowell.core.features.fileMetadata.dto.PreSignedUrlResponse;
import com.innowell.core.features.fileMetadata.enums.fileMetadataEnum;
import com.innowell.core.features.fileMetadata.repository.FileMetadataRepository;
import com.innowell.core.features.fileMetadata.utlis.FileMetadataDateUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.*;

@Service
public class FileMetadataServiceImpl implements FileMetadataService {

    private final AmazonS3 amazonS3;

    private final FileMetadataRepository fileMetadataRepository;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public FileMetadataServiceImpl(AmazonS3 amazonS3, FileMetadataRepository fileMetadataRepository) {
        this.amazonS3 = amazonS3;
        this.fileMetadataRepository = fileMetadataRepository;
    }

    @Override
    public PreSignedUrlResponse getPreSignedUrl(String fileName, String fileType, String source, String sourceId) {
        if(!fileMetadataEnum.isValidSource(source)) {
            throw new IllegalArgumentException("Invalid source type: " + source);
        }

        if(Objects.isNull(sourceId) || sourceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Source ID is required and cannot be null or empty " + sourceId);
        }

        Date expiration = new Date(System.currentTimeMillis() + 3600000); //1 hours
        String key = generateFileKey(fileName, source, sourceId);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        FileMatadata metadata = new FileMatadata(fileName, fileType, key, source, sourceId);
        fileMetadataRepository.save(metadata);
        return new PreSignedUrlResponse(metadata.getId(), url.toString());

    }

    @Override
    public Boolean acknowledgeClientUpload(String fileId, Long fileSize) {
        Optional<FileMatadata> metadata = fileMetadataRepository.findById(fileId);
        if (metadata.isPresent()) {
            metadata.get().setFileSize(fileSize);
            fileMetadataRepository.save(metadata.get());
            return true;
        }
        return false;
    }

    @Override
    public String getUrlWithExpiryAndId(String id, Integer days) {

        // Calculate the expiry date by adding the provided number of days to the current date.
        Date expiryDate = FileMetadataDateUtils.addTime(days);

        // Retrieve file metadata based on the provided file ID.
        Optional<FileMatadata> optionalFileMetadata = fileMetadataRepository.findById(id);
        
        // If no metadata is found for the given ID, throw a custom exception.
        if (optionalFileMetadata.isEmpty()) {
            throw new CustomInnowellException("File not found for Id " + id);
        }
        
        // Create a presigned URL request to retrieve the file from S3 with an expiry date.
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName,optionalFileMetadata.get().getKey())
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiryDate);
        
        // Generate the presigned URL from the request and return it as a string.
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        
        return url.toString();
    }

    @Override
    public Map<String, String> getUrlWithExpiryForMultipleFiles(String ids, Integer days) {
        Map<String, String> urls = new HashMap<>();
        List<String> fileIds = Arrays.asList(ids.split(","));

        for (String id : fileIds) {
            String url = getUrlWithExpiryAndId(id, days);
            urls.put(id, url);
        }
        return urls;
    }

    private String generateFileKey(String fileName, String source, String sourceId) {
        return source + "/" + sourceId + "/" + UUID.randomUUID() + "-" + formatFileName(fileName);
    }

    private String formatFileName(String fileName) {
        if (StringUtils.hasText(fileName)) {
            return fileName
                    .replace(" ", "_")
                    .replace(",", "_")
                    .replace(":", "_")
                    .replace("?", "_")
                    .replace("/", "_")
                    .replace("\\", "_")
                    .replace("<", "_")
                    .replace(">", "_")
                    .replace("|", "_")
                    .replace("%20", "_");
        }
        return fileName;
    }
}
