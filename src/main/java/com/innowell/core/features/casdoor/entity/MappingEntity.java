package com.innowell.core.features.casdoor.entity;

import com.innowell.core.core.models.Site;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "org_site_mapping_table") // Specify the table name
public class MappingEntity {

    @Id
    @UuidGenerator
    private String id; // Primary key for the mapping table

    @Column(name = "org_id", nullable = false)
    private String orgId; // Organization ID

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "status", nullable = false)
    private String status; // Status of the mapping

    // You can add additional constructors or methods if needed
}