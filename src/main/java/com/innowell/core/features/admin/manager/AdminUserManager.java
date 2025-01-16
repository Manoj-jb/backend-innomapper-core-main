package com.innowell.core.features.admin.manager;


import com.fasterxml.jackson.core.type.TypeReference;
import com.innowell.core.core.utils.http.CasdoorResponse;
import com.innowell.core.core.utils.http.Map;
import com.innowell.core.features.admin.enums.RoleOperations;
import com.innowell.core.features.admin.enums.UserOperations;
import com.innowell.core.features.admin.service.Service;
import com.innowell.core.features.auth.entity.Role;
import com.innowell.core.features.auth.entity.User;
import io.micrometer.common.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


@Component
public class AdminUserManager extends Service {
    public List<User> getUsers(String orgName) throws IOException {
        CasdoorResponse<List<User>, Object> resp = doGet("get-users",
                Map.of("owner", orgName), new TypeReference<CasdoorResponse<List<User>, Object>>() {});
        return resp.getData();
    }
    public List<User> getGlobalUsers() throws IOException {
        CasdoorResponse<List<User>, Object> response = doGet("get-global-users",null,
                new TypeReference<CasdoorResponse<List<User>, Object>>() {});

        return response.getData();
    }
    /**
     * Get users with pagination.
     * @param sorter sorter of users
     * @param limit limit of users to return. If limit is 0, then return all users.
     * @return list of users
     * @throws IOException if failed to get users
     */
    public List<User> getSortedUsers(String sorter, int limit, String orgName) throws IOException {
        CasdoorResponse<List<User>, Object> resp = doGet("get-users",
                Map.of("owner", orgName,
                        "sorter", sorter,
                        "limit", limit > 0 ? Integer.toString(limit) : ""), new TypeReference<CasdoorResponse<List<User>, Object>>() {});
        return resp.getData();
    }

    public int getUserCount(String isOnline, String orgName) throws IOException {
        CasdoorResponse<Integer, Object> resp = doGet("get-user-count",
                Map.of("owner", orgName,
                        "isOnline", isOnline), new TypeReference<CasdoorResponse<Integer, Object>>() {});
        return resp.getData();
    }

    public User getUser(String name, String orgName) throws IOException {
        CasdoorResponse<User, Object> resp = doGet("get-user",
                Map.of("id", orgName + "/" + name), new TypeReference<CasdoorResponse<User, Object>>() {});
        return objectMapper.convertValue(resp.getData(), User.class);
    }

    public User getUser(String combinedUsername) throws IOException {
        CasdoorResponse<User, Object> resp = doGet("get-user",
                Map.of("id", combinedUsername), new TypeReference<CasdoorResponse<User, Object>>() {});
        return objectMapper.convertValue(resp.getData(), User.class);
    }

    public User getUserByEmail(String email, String orgName) throws IOException {
        CasdoorResponse<User, Object> resp = doGet("get-user",
                Map.of("owner", orgName,
                        "email", email), new TypeReference<CasdoorResponse<User, Object>>() {});
        return resp.getData();
    }

    public CasdoorResponse<String, Object> updateUser(User user, String orgName) throws IOException {
        return modifyUser(UserOperations.UPDATE_USER, user, orgName);
    }

    public CasdoorResponse<String, Object> addUser(User user, String orgName) throws IOException {
        return modifyUser(UserOperations.ADD_USER, user, orgName);
    }

    public CasdoorResponse<String, Object> deleteUser(User user, String orgName) throws IOException {
        return modifyUser(UserOperations.DELETE_USER, user, orgName);
    }

    public CasdoorResponse<String, Object> updateUserById(String id, User user, String orgName) throws IOException {
        user.id = id;
        return updateUser(user, orgName);
    }

    private <T1, T2> CasdoorResponse<T1, T2> modifyUser(UserOperations method, User user, String orgName) throws IOException {
        String id = user.owner + "/" + user.name;
        user.owner = orgName;
        String payload = objectMapper.writeValueAsString(user);
        return doPost(method.getOperation(), Map.of(
                "id", id
        ), payload, new TypeReference<CasdoorResponse<T1, T2>>() {});
    }

}
