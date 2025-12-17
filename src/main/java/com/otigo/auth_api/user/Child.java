package com.otigo.auth_api.user;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "children")
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;

    // --- DÜZELTME BURADA YAPILDI ---
    // Eskiden: Set<User> experts;
    // Yeni Hali: Set<Expert> experts;
    // mappedBy = "trackedChildren" artık Expert sınıfındaki listeyi bulabilecek.
    @ManyToMany(mappedBy = "trackedChildren")
    private Set<Expert> experts = new HashSet<>(); 

    // Çocuğun Velisi (Parent)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private UserEntity parent;

    @OneToMany
    private List<ExpertRecommendation> recommendations; 

    public Child() {
    }

    // --- Getter ve Setterlar ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    // Buradaki tipi de User'dan Expert'e çevirdik
    public Set<Expert> getExperts() { return experts; }
    public void setExperts(Set<Expert> experts) { this.experts = experts; }

    public UserEntity getParent() { return parent; }
    public void setParent(UserEntity parent) { this.parent = parent; }
}