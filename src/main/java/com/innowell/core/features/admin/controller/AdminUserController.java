package com.innowell.core.features.admin.controller;

import com.innowell.core.features.admin.dto.RolesDto;
import com.innowell.core.features.admin.entity.Organization;
import com.innowell.core.features.admin.manager.AdminOrganizationManager;
import com.innowell.core.features.admin.manager.AdminRoleManager;
import com.innowell.core.features.admin.manager.AdminUserManager;
import com.innowell.core.features.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/innowell-mapper/admin/user")
public class AdminUserController {


    @Autowired
    AdminUserManager adminUserManager;

    @Autowired
    AdminRoleManager adminRoleManager;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getUsers(@RequestParam("orgName") String orgName) {
        List<User> users = null;
        try {
            users = adminUserManager.getUsers(orgName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(users);
    }

    @PostMapping("/update-role")
    public ResponseEntity<String> updatedRoles(@RequestParam("orgName") String orgName, @RequestParam("userName") String userName, @RequestBody RolesDto rolesDto) {
        adminRoleManager.updateRoleForUser(orgName, orgName + "/"+ userName, rolesDto);
        return ResponseEntity.ok("success");
    }



}
