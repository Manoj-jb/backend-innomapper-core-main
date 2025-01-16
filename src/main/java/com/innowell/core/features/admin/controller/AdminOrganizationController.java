package com.innowell.core.features.admin.controller;

import com.innowell.core.core.utils.http.CasdoorResponse;
import com.innowell.core.features.admin.entity.Application;
import com.innowell.core.features.admin.entity.Organization;
import com.innowell.core.features.admin.manager.AdminAuthManager;
import com.innowell.core.features.admin.manager.AdminOrganizationManager;
import com.innowell.core.features.auth.dto.TokenResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/innowell-mapper/admin/organization")
public class AdminOrganizationController {


    @Autowired
    AdminOrganizationManager adminOrganizationManager;

    @GetMapping("/all")
    public ResponseEntity<List<Organization>> getOrganizations() {
        List<Organization> orgs = null;
        try {
            orgs = adminOrganizationManager.getOrganizations();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(orgs);
    }

    @PostMapping("/add")
    public ResponseEntity<Application> addOrganization(@RequestBody Organization organization) {
        try {
            if(organization.displayName == null) {
                organization.displayName = organization.name;
            }
            return adminOrganizationManager.addOrganization(organization);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @PutMapping("/update")
    public ResponseEntity<String> updateOrganization(@RequestBody Organization organization) {
        try {
            adminOrganizationManager.updateOrganization(organization);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("success");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteOrganization(@RequestBody Organization organization) {
        try {
            adminOrganizationManager.deleteOrganization(organization);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("success");
    }

}
