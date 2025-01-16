package com.innowell.core.features.admin.manager;


import com.fasterxml.jackson.core.type.TypeReference;
import com.innowell.core.core.utils.http.CasdoorResponse;
import com.innowell.core.core.utils.http.Map;
import com.innowell.core.features.admin.dto.UpdateClientDto;
import com.innowell.core.features.admin.entity.Application;
import com.innowell.core.features.admin.entity.Organization;

import com.innowell.core.features.admin.enums.OrganizationOperations;
import com.innowell.core.features.admin.enums.UpdateClientStatus;
import com.innowell.core.features.admin.service.Service;
import com.innowell.core.features.admin.service.UpdateClientService;
import com.innowell.core.features.auth.entity.LoginUrl;
import com.innowell.core.features.auth.repository.LoginUrlRepository;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


@Component
public class AdminOrganizationManager extends Service {

    @Autowired
    AdminApplicationManager adminApplicationManager;

    @Autowired
    AdminRoleManager adminRoleManager;

    @Autowired
    UpdateClientService updateClientService;

    @Autowired
    LoginUrlRepository loginUrlRepository;

    @Value("${casdoor.endpoint}")
    private String endpoint;


    public List<Organization> getOrganizations() throws IOException {
        CasdoorResponse<List<Organization>, Object> resp = doGet("get-organizations",
                Map.of("owner", "admin"), new TypeReference<CasdoorResponse<List<Organization>, Object>>() {
                });
        return resp.getData();
    }

    public ResponseEntity<Application> addOrganization(Organization organization) throws IOException {
        organization.owner = "admin";
        modifyOrganization(OrganizationOperations.ADD_ORGANIZATION, organization);
        adminRoleManager.addFixedRole("viewer", organization.name);
        adminRoleManager.addFixedRole("admin", organization.name);
        adminRoleManager.addFixedRole("project_engineer", organization.name);

        CasdoorResponse<String, Object> applicationCreatedResponse = adminApplicationManager.createApplication(organization.name, organization.favicon, organization.name, organization.displayName);

        Application application = adminApplicationManager.getApplication(organization.name);

        LoginUrl loginUrl = createLoginUrl(application);
        try {
            updateClientService.saveClient(new UpdateClientDto(organization.name, UpdateClientStatus.NEW.name(), organization.displayName, organization.name, organization.websiteUrl, "admin@innowell.com", "admin"));
        } catch (Exception e) {}
        return ResponseEntity.ok(application);
    }

    public CasdoorResponse<String, Object> updateOrganization(Organization organization) throws IOException {
        organization.owner = "admin";
        try {
            updateClientService.saveClient(new UpdateClientDto(organization.name, UpdateClientStatus.EDIT.name(), organization.displayName, organization.name, organization.websiteUrl, "admin@innowell.com", "admin"));
        } catch (Exception e) {}
        return modifyOrganization(OrganizationOperations.UPDATE_ORGANIZATION, organization);
    }

    public CasdoorResponse<String, Object> deleteOrganization(Organization organization) throws IOException {
        organization.owner = "admin";
        try {
            updateClientService.saveClient(new UpdateClientDto(organization.name, UpdateClientStatus.REMOVE.name(), organization.displayName, organization.name, organization.websiteUrl, "admin@innowell.com", "admin"));
        } catch (Exception e) {}
        return modifyOrganization(OrganizationOperations.DELETE_ORGANIZATION, organization);
    }

    /**
     * modifyOrganization is an encapsulation of organization CUD(Create, Update, Delete) operations.
     * Possible actions are `add-organization`, `update-organization`, `delete-organization`.
     */
    private <T1, T2> CasdoorResponse<T1, T2> modifyOrganization(OrganizationOperations method, Organization organization) throws IOException {
        String id = organization.owner + "/" + organization.name;
        String payload = objectMapper.writeValueAsString(organization);
        return doPost(method.getOperation(),
                Map.of("id", id), payload
                , new TypeReference<CasdoorResponse<T1, T2>>() {
                });
    }


    private LoginUrl createLoginUrl(Application application) throws IOException {

        try {
            LoginUrl loginUrl = new LoginUrl();
            loginUrl.setClientId(application.clientId);
            loginUrl.setClientSecret(application.clientSecret);
            loginUrl.setClientName(application.name);
            loginUrl.setClientCertificate(getCertificate());
            loginUrl.setId(application.name);
            String url = endpoint + "/login/oauth/authorize?client_id=" + application.clientId + "&response_type=code&redirect_uri=https://innomapper-stage.pages.dev/callback&scope=read&state=" + application.name;
            loginUrl.setLoginUrl(url);
            return loginUrlRepository.save(loginUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String getCertificate() {
        return "-----BEGIN CERTIFICATE-----\n" +
                "MIIE3TCCAsWgAwIBAgIDAeJAMA0GCSqGSIb3DQEBCwUAMCgxDjAMBgNVBAoTBWFk\n" +
                "bWluMRYwFAYDVQQDEw1jZXJ0LWJ1aWx0LWluMB4XDTI0MDkyMzExMzMzN1oXDTQ0\n" +
                "MDkyMzExMzMzN1owKDEOMAwGA1UEChMFYWRtaW4xFjAUBgNVBAMTDWNlcnQtYnVp\n" +
                "bHQtaW4wggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQC3+ER9nSAE+up4\n" +
                "dS41QW6iByjTMQJ7lG+VhQp7Cg7TNYRV8r3nTTr4wnAo6cPLE+Ya/ZEwzl2RGxrf\n" +
                "AJUQVjGMe7VNN5dtPQwLx5jioDjqsrHmPiL3m9C/uioYufq3LPo7BoX7X63Oh5w/\n" +
                "yXyczEMs89ZOwmulrKtfvrV/PA2aGuiMnHpbFcQ96MGOKQeR0I5fAYSWmOyBVR13\n" +
                "GUBxZ5sofomT/dEOXq4R2PMIz49CrF5x8fbCeXxrt1OEf+WII4ja4qmQU2KbvsN8\n" +
                "7c2pO6hkPgNf8YGVIpAIxexxZXYzUAUxco53pq2VMhwdkNIlSLwSj9TtdeL3gyHX\n" +
                "t5PXRitlM8a5B64yl5a8qtV31EHqbmhvIvQJMrzDbVEqfY9AMcl5AfrM9BTmdjJQ\n" +
                "WRq8YL0QzJjvBnnFh5sJcHwR8NT+YOvwM07XG5VmOmj+v5Srjya9rZA/tQ9a41GJ\n" +
                "+S0po15OaOr+nTteIlN7Nz4udUS05DLaYi/KpWRKqP69XtfO33WOd7oe9PKmM+u8\n" +
                "RuJPD4awdKxA6MGKVpyglk9cQ2fW2gQRsNVwQYarYE+8FzEZxhYDD0P/NfjDO/ZK\n" +
                "2MO9hEHQslbkYNtNGsac5ktMpUGjB3jrlYbJ7P7kn9DcEHdYeG41eYouC0hCveoX\n" +
                "L/J8IriszVk8mXW57slGFfK3ova6vwIDAQABoxAwDjAMBgNVHRMBAf8EAjAAMA0G\n" +
                "CSqGSIb3DQEBCwUAA4ICAQCWiLaE5HwWQUr+5P3LiyI1MBrKhe+tPVQQvjaDaNRe\n" +
                "nT200xNZQby83bUNI2kmJZJACFbxkjl9hVVQBC8QryzCfKoS6PWRYfK4M9TRyqJI\n" +
                "3w9F3YXc9Vc+htYKDYrTrG7Zx4w4WIAX4L/zPvVRiX3wlRsLp9DySb6Hr7JWTQbH\n" +
                "MzsMjkYLETcso7PB8ODHuIuYNjs/S3bsrkCCQ08v+TWdEZLlA/jagPCJ4jYnOt6/\n" +
                "/UU7TxtZgu0v9J0zZC4ShBb5IkLokwfL45MxJrbJ0qKmgSawDT/I+a5ocDs/FAqt\n" +
                "OF1/O6ctnhPsrhuI9WiJj08tnNvQLEd4rg5++mbvXrO8ofFsk8cVSTVntAUa/MXP\n" +
                "CWeCDMy+AKSx55MrAMbb+uqae/S5mgTHKlj5h9Asrn2O9VwW9qO8cto/T4WYZL42\n" +
                "2QWJv46KBOKGXDtPqmMUl9C+0NYJw9mbntzh7ddHteAtSj3k29QJiRuq5oumn3+P\n" +
                "PgQZg0yj2u98KUYMDU2A0D47i2NDClLE9PSBUCfPefJRpOWM3+KQKKJn7OaWFMHN\n" +
                "MP0XzjrfL9WsYRsNTOr64Rxg+KQIrdqqLjfR+C5RHnn7j5Rtmbq0pml2T6KlJeal\n" +
                "N8Qf9wQFSskbKx7NfsSQoXEXgbnF9gujXCYDZfT+fWGr8/1FolfDjCvo2pMm82Yk\n" +
                "hQ==\n" +
                "-----END CERTIFICATE-----";
    }

}
