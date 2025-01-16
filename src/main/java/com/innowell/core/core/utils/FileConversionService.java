package com.innowell.core.core.utils;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

@Service
public class FileConversionService {

    public String convertFileToBase64(String fileUrl) {
        try {
            // Open a connection to the URL
            URL url = new URL(fileUrl);
            InputStream inputStream = url.openStream();

            // Read the bytes from the InputStream
            byte[] fileBytes = inputStream.readAllBytes();

            // Close the InputStream
            inputStream.close();

            // Convert bytes to Base64
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            // Handle exceptions appropriately
            throw new RuntimeException("Failed to convert file to Base64", e);
        }
    }
}