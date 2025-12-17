package com.otigo.auth_api.user;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "experts")
@PrimaryKeyJoinColumn(name = "user_id")
public class Expert extends UserEntity {

    // Uzmanların takip ettiği çocuklar listesi
    @ManyToMany
    @JoinTable(
        name = "expert_tracked_children",
        joinColumns = @JoinColumn(name = "expert_id"),
        inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private Set<Child> trackedChildren = new HashSet<>();

    public Expert() {
    }

    // Getter ve Setter
    public Set<Child> getTrackedChildren() {
        return trackedChildren;
    }

    public void setTrackedChildren(Set<Child> trackedChildren) {
        this.trackedChildren = trackedChildren;
    }
}