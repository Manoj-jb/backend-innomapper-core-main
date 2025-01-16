package com.innowell.core.features.admin.manager;


import com.fasterxml.jackson.core.type.TypeReference;
import com.innowell.core.core.utils.http.CasdoorResponse;
import com.innowell.core.core.utils.http.Map;
import com.innowell.core.features.admin.entity.Application;
import com.innowell.core.features.admin.enums.ApplicationOperations;
import com.innowell.core.features.admin.service.Service;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


@Component
public class AdminApplicationManager extends Service {
    public Application getApplication(String name) throws IOException {
        CasdoorResponse<Application, Object> response = doGet("get-application",
                Map.of("id", "admin/" + name), new TypeReference<CasdoorResponse<Application, Object>>() {
                });
        return response.getData();
    }

    public List<Application> getApplications() throws IOException {
        CasdoorResponse<List<Application>, Object> response = doGet("get-applications",
                Map.of("owner", "admin"), new TypeReference<CasdoorResponse<List<Application>, Object>>() {
                });
        return response.getData();
    }

    public List<Application> getOrganizationApplications(String orgName) throws IOException {
        CasdoorResponse<List<Application>, Object> response = doGet("get-organization-applications",
                Map.of("owner", "admin", "organization", orgName),
                new TypeReference<CasdoorResponse<List<Application>, Object>>() {
                });
        return response.getData();
    }

    public CasdoorResponse<String, Object> addApplication(Application application) throws IOException {
        application.owner = "admin";
        return modifyApplication(ApplicationOperations.ADD_APPLICATION, application, null);
    }

    public CasdoorResponse<String, Object> deleteApplication(Application application) throws IOException {
        application.owner = "admin";
        return modifyApplication(ApplicationOperations.DELETE_APPLICATION, application, null);
    }

    public CasdoorResponse<String, Object> updateApplication(Application application) throws IOException {
        application.owner = "admin";
        return modifyApplication(ApplicationOperations.UPDATE_APPLICATION, application, null);
    }

    private <T1, T2> CasdoorResponse<T1, T2> modifyApplication(ApplicationOperations method, Application application, java.util.Map<String, String> queryMap) throws IOException {
        String id = application.owner + "/" + application.name;
        String payload = objectMapper.writeValueAsString(application);
        return doPost(method.getOperation(), Map.mergeMap(Map.of("id", id), queryMap), payload,
                new TypeReference<CasdoorResponse<T1, T2>>() {
                });
    }

    private <T1, T2> CasdoorResponse<T1, T2> modifyApplication(ApplicationOperations method, String payload, String applicationName, java.util.Map<String, String> queryMap) throws IOException {
        String id = "admin/" + applicationName;

        return doPost(method.getOperation(), Map.mergeMap(Map.of("id", id), queryMap), payload,
                new TypeReference<CasdoorResponse<T1, T2>>() {
                });
    }

    public CasdoorResponse<String, Object> createApplication(String applicationName, String logoUrl, String organizationName, String orgDisplayName) throws IOException {

        String body =
                "{\n" +
                        "    \"owner\": \"admin\",\n" +
                        "    \"name\": \"" + applicationName + "\",\n" +
                        "    \"organization\": \"" + organizationName + "\",\n" +
                        "    \"displayName\":\"" + orgDisplayName + "\",\n" +
                        "    \"logo\": \"" + logoUrl + "\",\n" +
                        "    \"enablePassword\": true,\n" +
                        "    \"enableSignUp\": true,\n" +
                        "    \"enableSigninSession\": false,\n" +
                        "    \"enableCodeSignin\": false,\n" +
                        "    \"enableSamlCompress\": false,\n" +
                        "    \"footerHtml\": \"<footer class=\\\"custom-footer\\\">\\n    <div class=\\\"footer-container\\\">\\n        <div class=\\\"footer-logo\\\">\\n            <img src=\\\"https://iili.io/dLHpyv4.png\\\" height=\\\"25\\\" />\\n            <span>Innowell Group</span>\\n        </div>\\n    </div>\\n</footer>\",\n" +
                        "    \"themeData\": {\"themeType\": \"default\", \"colorPrimary\": \"#01BCB1\", \"borderRadius\": 4, \"isCompact\": true, \"isEnabled\": true},\n" +
                        "    \"providers\": [\n" +
                        "        {\n" +
                        "            \"owner\": \"\",\n" +
                        "            \"name\": \"azure_ad_provider\",\n" +
                        "            \"canSignUp\": true,\n" +
                        "            \"canSignIn\": true,\n" +
                        "            \"canUnlink\": true,\n" +
                        "            \"countryCodes\": null,\n" +
                        "            \"prompted\": true,\n" +
                        "            \"signupGroup\": \"\",\n" +
                        "            \"rule\": \"None\",\n" +
                        "            \"provider\": {\n" +
                        "                \"owner\": \"admin\",\n" +
                        "                \"name\": \"azure_ad_provider\",\n" +
                        "                \"displayName\": \"Microsoft Account \",\n" +
                        "                \"category\": \"OAuth\",\n" +
                        "                \"type\": \"AzureAD\",\n" +
                        "                \"subType\": \"\",\n" +
                        "                \"method\": \"Normal\",\n" +
                        "                \"clientId\": \"a89c3485-54d6-48a5-a3cb-406159d93725\",\n" +
                        "                \"clientSecret\": \"***\",\n" +
                        "                \"clientId2\": \"\",\n" +
                        "                \"clientSecret2\": \"\",\n" +
                        "                \"cert\": \"\",\n" +
                        "                \"customAuthUrl\": \"\",\n" +
                        "                \"customTokenUrl\": \"\",\n" +
                        "                \"customUserInfoUrl\": \"\",\n" +
                        "                \"customLogo\": \"https://iili.io/27uq71t.th.png\",\n" +
                        "                \"scopes\": \"\",\n" +
                        "                \"userMapping\": {\n" +
                        "                    \"avatarUrl\": \"avatarUrl\",\n" +
                        "                    \"displayName\": \"displayName\",\n" +
                        "                    \"email\": \"email\",\n" +
                        "                    \"id\": \"id\",\n" +
                        "                    \"username\": \"username\"\n" +
                        "                },\n" +
                        "                \"host\": \"\",\n" +
                        "                \"port\": 0,\n" +
                        "                \"disableSsl\": false,\n" +
                        "                \"title\": \"\",\n" +
                        "                \"content\": \"\",\n" +
                        "                \"receiver\": \"\",\n" +
                        "                \"regionId\": \"\",\n" +
                        "                \"signName\": \"\",\n" +
                        "                \"templateCode\": \"\",\n" +
                        "                \"appId\": \"\",\n" +
                        "                \"endpoint\": \"\",\n" +
                        "                \"intranetEndpoint\": \"\",\n" +
                        "                \"domain\": \"\",\n" +
                        "                \"bucket\": \"\",\n" +
                        "                \"pathPrefix\": \"\",\n" +
                        "                \"metadata\": \"\",\n" +
                        "                \"idP\": \"\",\n" +
                        "                \"issuerUrl\": \"\",\n" +
                        "                \"enableSignAuthnRequest\": false,\n" +
                        "                \"providerUrl\": \"https://login.microsoftonline.com/common/oauth2/v2.0/authorize\"\n" +
                        "            }\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"owner\": \"\",\n" +
                        "            \"name\": \"provider_captcha_default\",\n" +
                        "            \"canSignUp\": false,\n" +
                        "            \"canSignIn\": false,\n" +
                        "            \"canUnlink\": false,\n" +
                        "            \"countryCodes\": null,\n" +
                        "            \"prompted\": false,\n" +
                        "            \"signupGroup\": \"\",\n" +
                        "            \"rule\": \"\",\n" +
                        "            \"provider\": {\n" +
                        "                \"owner\": \"admin\",\n" +
                        "                \"name\": \"provider_captcha_default\",\n" +
                        "                \"displayName\": \"Captcha Default\",\n" +
                        "                \"category\": \"Captcha\",\n" +
                        "                \"type\": \"Default\",\n" +
                        "                \"subType\": \"\",\n" +
                        "                \"method\": \"\",\n" +
                        "                \"clientId\": \"\",\n" +
                        "                \"clientSecret\": \"\",\n" +
                        "                \"clientId2\": \"\",\n" +
                        "                \"clientSecret2\": \"\",\n" +
                        "                \"cert\": \"\",\n" +
                        "                \"customAuthUrl\": \"\",\n" +
                        "                \"customTokenUrl\": \"\",\n" +
                        "                \"customUserInfoUrl\": \"\",\n" +
                        "                \"customLogo\": \"\",\n" +
                        "                \"scopes\": \"\",\n" +
                        "                \"userMapping\": null,\n" +
                        "                \"host\": \"\",\n" +
                        "                \"port\": 0,\n" +
                        "                \"disableSsl\": false,\n" +
                        "                \"title\": \"\",\n" +
                        "                \"content\": \"\",\n" +
                        "                \"receiver\": \"\",\n" +
                        "                \"regionId\": \"\",\n" +
                        "                \"signName\": \"\",\n" +
                        "                \"templateCode\": \"\",\n" +
                        "                \"appId\": \"\",\n" +
                        "                \"endpoint\": \"\",\n" +
                        "                \"intranetEndpoint\": \"\",\n" +
                        "                \"domain\": \"\",\n" +
                        "                \"bucket\": \"\",\n" +
                        "                \"pathPrefix\": \"\",\n" +
                        "                \"metadata\": \"\",\n" +
                        "                \"idP\": \"\",\n" +
                        "                \"issuerUrl\": \"\",\n" +
                        "                \"enableSignAuthnRequest\": false,\n" +
                        "                \"providerUrl\": \"\"\n" +
                        "            }\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"SigninMethods\": [\n" +
                        "        {\n" +
                        "            \"name\": \"Password\",\n" +
                        "            \"displayName\": \"Password\",\n" +
                        "            \"rule\": \"All\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"Verification code\",\n" +
                        "            \"displayName\": \"Verification code\",\n" +
                        "            \"rule\": \"All\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"WebAuthn\",\n" +
                        "            \"displayName\": \"WebAuthn\",\n" +
                        "            \"rule\": \"None\"\n" +
                        "        }\n" +
                        "    ],\n"+
                        "    \"SigninItems\": [\n" +
                        "       {\n" +
                        "            \"name\": \"Back button\",\n" +
                        "            \"visible\": true,\n" +
                        "            \"label\": \"\",\n" +
                        "            \"customCss\": \".back-button {\\n      top: 65px;\\n      left: 15px;\\n      position: absolute;\\n}\\n.back-inner-button{}\",\n" +
                        "            \"placeholder\": \"\",\n" +
                        "            \"rule\": \"None\",\n" +
                        "            \"isCustom\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"Languages\",\n" +
                        "            \"visible\": true,\n" +
                        "            \"label\": \"\",\n" +
                        "            \"customCss\": \".login-languages {\\n    top: 55px;\\n    right: 5px;\\n    position: absolute;\\n}\",\n" +
                        "            \"placeholder\": \"\",\n" +
                        "            \"rule\": \"None\",\n" +
                        "            \"isCustom\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"Logo\",\n" +
                        "            \"visible\": true,\n" +
                        "            \"label\": \"\",\n" +
                        "            \"customCss\": \".login-logo-box {\\n    text-align: left; /* Aligns text and inline elements to the left */\\n}\\n.login-logo-box img {\\n    display: block; /* Ensures the image is treated as a block element */\\n    margin-left: 0; /* Ensures no left margin is applied */\\n    margin-right: auto; /* Automatically adjusts right margin to push the image to the left */\\n}\",\n" +
                        "            \"placeholder\": \"\",\n" +
                        "            \"rule\": \"None\",\n" +
                        "            \"isCustom\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"Signin methods\",\n" +
                        "            \"visible\": true,\n" +
                        "            \"label\": \"\",\n" +
                        "            \"customCss\": \".signin-methods {}\",\n"+
                        "           \"placeholder\": \"\",\n"+
                        "           \"rule\": \"None\",\n"+
                        "           \"isCustom\": false\n"+
                        "        },\n"+
                        "        {\n"+
                        "           \"name\":\"Username\",\n"+
                        "           \"visible\": true,\n" +
                        "           \"label\":\"\",\n"+
                        "           \"customCss\":\".login-username {}\\n.login-username-input{}\",\n"+
                        "           \"placeholder\":\"\",\n"+
                        "           \"rule\":\"None\",\n"+
                        "            \"isCustom\": false \r\n" +
                        "        },\n"+
                        "        {\n"+
                        "           \"name\":\"Password\",\n"+
                        "           \"visible\": true,\n" +
                        "           \"label\":\"\",\n"+
                        "           \"customCss\":\".login-password {}\\n.login-password-input{}\",\n"+
                        "           \"placeholder\":\"\",\n"+
                        "           \"rule\":\"None\",\n"+
                        "           \"isCustom\": false \r\n" +
                        "        },\n"+
                        "        {\n"+
                        "           \"name\":\"Agreement\",\n"+
                        "           \"visible\": true,\n" +
                        "           \"label\":\"\",\n"+
                        "           \"customCss\":\".login-agreement {}\",\n"+
                        "           \"placeholder\":\"\",\r\n"+
                        "           \"rule\":\"None\", \r\n"+
                        "            \"isCustom\": false \r\n" +
                        "        }, \r\n"+
                        "        {\r\n"+
                        "          \"name\":\"Forgot password?\", \r\n"+
                        "          \"visible\": true,\n" +
                        "          \"label\":\"\", \r\n"+
                        "          \"customCss\":\".login-forget-password {\\r\\n display: inline-flex;\\r\\n justify-content: space-between;\\r\\n width: 320px;\\r\\n margin-bottom: 25px; \\r\\nd}\", \r\n"+
                        "          \"placeholder\":\"\", \r\n"+
                        "          \"rule\":\"None\", \r\n"+
                        "          \"isCustom\": false \r\n" +
                        "        },\r\n"+
                        "        {\r\n"+
                        "          \"name\":\"Login button\", \r\n"+
                        "          \"visible\": true,\n" +
                        "          \"label\":\"\", \r\n"+
                        "          \"customCss\":\".login-button-box { \\r\\n margins-bottom: 5px; \\r\\nd}\\r\\nd.login-button { \\r\\n width: 100%; \\r\\nd}\", \r\n"+
                        "          \"placeholder\":\"\", \r\n"+
                        "          \"rule\":\"None\", \r\n"+
                        "           \"isCustom\": false \r\n" +
                        "        },\r\n"+
                        "        {\r\n" +
                        "            \"name\": \"Signup link\", \r\n" +
                        "            \"visible\": true, \r\n" +
                        "            \"label\": \"No Account ? SIGNUP NOW\", \r\n" +
                        "            \"customCss\": \".login-signup-link { margin-bottom: 24px; \\n display: flex; \\n justify-content: end; }\", \r\n" +
                        "            \"placeholder\": \"\", \r\n" +
                        "            \"rule\": \"None\", \r\n" +
                        "            \"isCustom\": false \r\n" +
                        "        },\r\n" +
                        "        {\r\n" +
                        "            \"name\": \"Providers\", \r\n" +
                        "            \"visible\": true, \r\n" +
                        "            \"label\": \"\", \r\n" +
                        "            \"customCss\": \".provider-img { width: 30px; margin: 5px; } \\n .provider-big-img { margin-bottom: 10px; }\", \r\n" +
                        "            \"placeholder\": \"\", \r\n" +
                        "            \"rule\": \"big\", \r\n" +
                        "            \"isCustom\": false \r\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"signupItems\": [\n" +
                        "        {\n" +
                        "            \"name\": \"ID\",\n" +
                        "            \"visible\": false,\n" +
                        "            \"required\": true,\n" +
                        "            \"rule\": \"Random\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"Username\",\n" +
                        "            \"visible\": true,\n" +
                        "            \"required\": true,\n" +
                        "            \"rule\": \"None\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"Display name\",\n" +
                        "            \"visible\": true,\n" +
                        "            \"required\": true,\n" +
                        "            \"rule\": \"None\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"Password\",\n" +
                        "            \"visible\": true,\n" +
                        "            \"required\": true,\n" +
                        "            \"rule\": \"None\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"Confirm password\",\n" +
                        "            \"visible\": true,\n" +
                        "            \"required\": true,\n" +
                        "            \"rule\": \"None\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"Agreement\",\n" +
                        "            \"visible\": true,\n" +
                        "            \"required\": true,\n" +
                        "            \"rule\": \"None\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"Signup button\",\n" +
                        "            \"visible\": true,\n" +
                        "            \"required\": true,\n" +
                        "            \"rule\": \"None\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"Providers\",\n" +
                        "            \"visible\": true,\n" +
                        "            \"required\": true,\n" +
                        "            \"rule\": \"None\",\n" +
                        "            \"customCss\": \".provider-img {\\n width: 30px;\\n margin: 5px;\\n }\\n .provider-big-img {\\n margin-bottom: 10px;\\n }\\n \"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"grantTypes\": [\n" +
                        "        \"authorization_code\",\n" +
                        "        \"password\",\n" +
                        "        \"client_credentials\",\n" +
                        "        \"token\",\n" +
                        "        \"id_token\",\n" +
                        "        \"refresh_token\"\n" +
                        "    ],\n" +
                        "    \"cert\": \"cert-built-in\",\n" +
                        "    \"redirectUris\": [\n" +
                        "        \"http://localhost:8081/innowell-mapper/auth/token/login\",\n" +
                        "        \"https://backend-stage.innowell.vidyayatan.com/innowell-mapper/auth/token/login\",\n" +
                        "        \"https://testing-frontend-innomapper.pages.dev/callback\",\n" +
                        "        \"https://innomapper-stage.pages.dev/callback\"\n" +
                        "    ],\n" +
                        "    \"tokenFormat\": \"JWT\",\n" +
                        "    \"tokenFields\": [],\n" +
                        "    \"expireInHours\": 168,\n" +
                        "    \"refreshExpireInHours\": 168,\n" +
                        "    \"formOffset\": 2\n" +
                        "}";

        return modifyApplication(ApplicationOperations.ADD_APPLICATION, body, applicationName, null);
    }
}
