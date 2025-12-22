package com.otigo.auth_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parents")
@PrimaryKeyJoinColumn(name = "user_id")
public class Parent extends UserEntity {
    
    // Veliye özel alanlar buraya gelir
    public Parent() {
    }
}