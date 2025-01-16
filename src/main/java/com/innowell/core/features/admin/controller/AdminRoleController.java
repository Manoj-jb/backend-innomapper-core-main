package com.innowell.core.features.admin.controller;

import com.innowell.core.features.admin.entity.Organization;
import com.innowell.core.features.admin.manager.AdminOrganizationManager;
import com.innowell.core.features.admin.manager.AdminRoleManager;
import com.innowell.core.features.auth.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/innowell-mapper/admin/role")
public class AdminRoleController {


    @Autowired
    AdminRoleManager adminRoleManager;

    @GetMapping("/all")
    public ResponseEntity<List<Role>> getRoles(@RequestParam("orgName") String orgName) {
        List<Role> roles = null;
        try {
            roles = adminRoleManager.getRoles(orgName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateRole(@RequestBody Role role, @RequestParam("orgName") String orgName) {
        try {
            adminRoleManager.updateRole(role, orgName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("success");
    }

}
