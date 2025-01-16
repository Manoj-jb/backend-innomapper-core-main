package com.innowell.core.features.auth.repository;

import com.innowell.core.features.auth.entity.LoginUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginUrlRepository extends JpaRepository<LoginUrl, String> {
    Optional<LoginUrl> findByClientName(String clientName);
}