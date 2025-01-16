package com.innowell.core.core.models;

import com.innowell.core.features.auth.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.stream.Collectors;

public class CustomUserDetails extends User implements UserDetails {

    private final String username;
    private final String password;

    @Getter
    private final String orgId;

    @Getter
    private final String userId;
    Collection<? extends GrantedAuthority> authorities;


    /**
     * Constructor for CustomUserDetails, creating an instance from a User object.
     *
     * @param user The User object to extract details from.
     */
    public CustomUserDetails(User user) {
        // Set the username from the provided User object
        this.username = user.email;
        this.orgId = user.owner;
        // Set the password securely from the User object
        this.password = user.password;
        this.userId = user.id;

        // Create a list to store GrantedAuthority objects
        List<GrantedAuthority> auths = new ArrayList<>();

        // Iterate through each UserRole for the user

        // Get individual authorities from the role and convert them to uppercase GrantedAuthority objects
        user.permissions.forEach(userAuthority -> auths.add(new SimpleGrantedAuthority(userAuthority.name.toUpperCase())));

        // Add the role name itself as a GrantedAuthority (also in uppercase)
        user.roles.forEach(userAuthority -> auths.add(new SimpleGrantedAuthority(userAuthority.name.toUpperCase())));

        // Assign the collected authorities to the this.authorities field
        this.authorities = auths;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getAuthority()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}