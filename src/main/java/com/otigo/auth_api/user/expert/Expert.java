package com.otigo.auth_api.user.expert;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.otigo.auth_api.user.Child;
import com.otigo.auth_api.user.UserEntity;


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

    @OneToMany(mappedBy= "expert", fetch = FetchType.LAZY)
    private List<ExpertRecommendation> recommendations;

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

    // Getter ve Setter
    public List<ExpertRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<ExpertRecommendation> recommendations) {
        this.recommendations = recommendations;
    }
}