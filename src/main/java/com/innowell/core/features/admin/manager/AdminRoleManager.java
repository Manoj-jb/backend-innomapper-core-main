package com.innowell.core.features.admin.manager;


import com.fasterxml.jackson.core.type.TypeReference;
import com.innowell.core.core.utils.http.CasdoorResponse;
import com.innowell.core.core.utils.http.Map;
import com.innowell.core.features.admin.dto.RolesDto;
import com.innowell.core.features.admin.enums.RoleOperations;
import com.innowell.core.features.admin.service.Service;
import com.innowell.core.features.auth.entity.Role;
import com.innowell.core.features.auth.entity.User;
import io.micrometer.common.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
public class AdminRoleManager extends Service {

    @Autowired
    AdminUserManager adminUserManager;

    public Role getRole(String name, String orgName) throws IOException {
        CasdoorResponse<Role, Object> resp = doGet("get-role",
                Map.of("id", orgName + "/" + name), new TypeReference<CasdoorResponse<Role, Object>>() {
                });
        return resp.getData();
    }

    public List<Role> getRoles(String orgName) throws IOException {
        CasdoorResponse<List<Role>, Object> resp = doGet("get-roles",
                Map.of("owner", orgName), new TypeReference<CasdoorResponse<List<Role>, Object>>() {
                });
        return resp.getData();
    }


    public CasdoorResponse<String, Object> updateRole(Role role, String orgName) throws IOException {
        return modifyRole(RoleOperations.UPDATE_ROLE, role, orgName);
    }

    public CasdoorResponse<String, Object> updateRoleForColumns(Role role, String orgName, String... columns) throws IOException {
        return modifyRole(RoleOperations.UPDATE_ROLE, role, orgName);
    }

    public CasdoorResponse<String, Object> addRole(Role role, String orgName) throws IOException {
        return modifyRole(RoleOperations.ADD_ROLE, role, orgName);
    }

    public CasdoorResponse<String, Object> deleteRole(Role role, String orgName) throws IOException {
        return modifyRole(RoleOperations.DELETE_ROLE, role, orgName);
    }

    private <T1, T2> CasdoorResponse<T1, T2> modifyRole(RoleOperations method, Role role, String orgName) throws IOException {
        String id = role.owner + "/" + role.name;
        role.owner = orgName;
        String payload = objectMapper.writeValueAsString(role);
        return doPost(method.getOperation(),
                Map.of("id", id), payload
                , new TypeReference<CasdoorResponse<T1, T2>>() {
                });
    }

    private <T1, T2> CasdoorResponse<T1, T2> modifyRole(RoleOperations method, String roleBody, String orgName, String roleName) throws IOException {
        String id = orgName + "/" + roleName;


        return doPost(method.getOperation(),
                Map.of("id", id), roleBody
                , new TypeReference<CasdoorResponse<T1, T2>>() {
                });
    }

    public CasdoorResponse<String, Object> addFixedRole(String roleName, String orgName) throws IOException {

        // Create the JSON body with dynamic values
        String jsonBody = String.format("{\n" +
                "    \"owner\": \"%s\",\n" +
                "    \"name\": \"%s\",\n" +
                "    \"createdTime\": \"2024-11-09T14:26:37+05:30\",\n" +
                "    \"displayName\": \"%s\",\n" +
                "    \"users\": [],\n" +
                "    \"groups\": [],\n" +
                "    \"roles\": [],\n" +
                "    \"domains\": [],\n" +
                "    \"isEnabled\": true\n" +
                "}", orgName, roleName, roleName); // Using roleName for displayName as well
        return modifyRole(RoleOperations.ADD_ROLE, jsonBody, orgName, roleName);
    }

    public void updateRoleForUser(String orgName, String userName, RolesDto rolesDto) {

        try {
            User user = adminUserManager.getUser(userName);

            if(rolesDto.getRoles().contains("admin")) {
                user.isAdmin = true;
            }
            else  user.isAdmin = false;
            adminUserManager.updateUser(user, orgName);

            List<Role> roles = getRoles(orgName);
            roles.forEach(role -> {
                if (rolesDto.getRoles().contains(role.name)) {
                    List<String> usersInThisRole = new ArrayList<>(List.of(role.users));
                    if (!usersInThisRole.contains(userName)) {
                        usersInThisRole.add(userName);
                        role.users = usersInThisRole.stream().toArray(String[]::new);
                        try {
                            updateRole(role, orgName);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                else {
                    List<String> usersInThisRole = new ArrayList<>(List.of(role.users));
                    if (usersInThisRole.contains(userName)) {
                        usersInThisRole.remove(userName);
                        role.users = usersInThisRole.stream().toArray(String[]::new);
                        try {
                            updateRole(role, orgName);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
