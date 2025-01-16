package com.innowell.core.features.admin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowell.core.core.utils.http.CasdoorResponse;
import com.innowell.core.core.utils.http.HttpClient;
import com.innowell.core.core.utils.http.Map;
import okhttp3.Credentials;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class Service {
    protected final ObjectMapper objectMapper = new ObjectMapper(){{
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }};
    @Value("${casdoor.endpoint}")
    private String endpoint;
    protected final String credential;


    protected Service() {
        this.credential = Credentials.basic("f9e5feced85b9017768a", "0d1ef57a14fb36403146a1180974f8017f8ba138");
    }

    protected <T1, T2> CasdoorResponse<T1, T2> doGet(@NotNull String action, @Nullable java.util.Map<String, String> queryParams, TypeReference<CasdoorResponse<T1, T2>> typeReference) throws IOException {
        String url = String.format("%s/api/%s?%s", endpoint, action, Map.mapToUrlParams(queryParams));
        String response = HttpClient.syncGet(url, credential);
        CasdoorResponse<T1, T2> resp = objectMapper.readValue(response, typeReference);
        if (!Objects.equals(resp.getStatus(), "ok")) {
            try {
                throw new Exception(String.format("Failed fetching %s : %s", url, resp.getMsg()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return resp;
    }

    protected <T1, T2> CasdoorResponse<T1, T2> doPost(@NotNull String action, @Nullable java.util.Map<String, String> queryParams, java.util.Map<String, String> postForm, TypeReference<CasdoorResponse<T1, T2>> typeReference) throws IOException {
        String url = String.format("%s/api/%s?%s", endpoint, action, Map.mapToUrlParams(queryParams));
        String response = HttpClient.postForm(url, postForm, credential);
        CasdoorResponse<T1, T2> resp = objectMapper.readValue(response, typeReference);
        if (!Objects.equals(resp.getStatus(), "ok")) {
            try {
                throw new Exception(String.format("Failed fetching %s : %s", url, resp.getMsg()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return resp;
    }

    protected <T1, T2> CasdoorResponse<T1, T2> doPost(@NotNull String action, @Nullable java.util.Map<String, String> queryParams, String postString, TypeReference<CasdoorResponse<T1, T2>> typeReference) throws IOException {
        String url = String.format("%s/api/%s?%s", endpoint, action, Map.mapToUrlParams(queryParams));
        String response = HttpClient.postString(url, postString, credential);
        CasdoorResponse<T1, T2> resp = objectMapper.readValue(response, typeReference);
        if (!Objects.equals(resp.getStatus(), "ok")) {
            try {
                throw new Exception(String.format("Failed fetching %s : %s", url, resp.getMsg()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return resp;
    }

    protected <T1, T2> CasdoorResponse<T1, T2> doPost(String action, @Nullable java.util.Map<String, String> queryParams, File postFile, TypeReference<CasdoorResponse<T1, T2>> typeReference) throws IOException {
        String url = String.format("%s/api/%s?%s", endpoint, action, Map.mapToUrlParams(queryParams));
        String response = HttpClient.postFile(url, postFile, credential);
        CasdoorResponse<T1, T2> resp = objectMapper.readValue(response, typeReference);
        if (!Objects.equals(resp.getStatus(), "ok")) {
            try {
                throw new Exception(String.format("Failed fetching %s : %s", url, resp.getMsg()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return resp;
    }
}