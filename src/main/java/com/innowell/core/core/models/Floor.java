package com.innowell.core.core.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "floor")
public class Floor {

    @Id
    @Column(name = "floor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long floorId;

    @Column(name = "number")
    private int number;

    @Column(name = "name", nullable = false)
    private String floorName;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "floor_geojson")
    private String floorGeojson;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;
}   
