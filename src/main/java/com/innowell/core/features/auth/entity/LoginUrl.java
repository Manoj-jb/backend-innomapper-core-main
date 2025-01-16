package com.innowell.core.features.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Entity
@Table(name = "login_urls")
@Data
public class LoginUrl {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "login_url", nullable = false, length = 500)
    private String loginUrl;

    @Column(name = "client_secret", nullable = false, length = 500)
    private String clientSecret;

    @Column(name = "client_certificate", nullable = false, length = 500)
    private String clientCertificate;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

}