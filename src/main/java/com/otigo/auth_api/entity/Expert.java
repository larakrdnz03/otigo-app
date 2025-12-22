package com.otigo.auth_api.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.ArrayList; // ArrayList importu eklendi
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "experts")
@PrimaryKeyJoinColumn(name = "user_id")
public class Expert extends UserEntity {

    // --- 1. TAKİP EDİLEN ÇOCUKLAR (İlişkinin Sahibi Burası) ---
    // Burada @JoinTable OLUR. Çünkü ara tabloyu biz yönetiyoruz.
    @ManyToMany
    @JoinTable(
        name = "expert_tracked_children",
        joinColumns = @JoinColumn(name = "expert_id"),
        inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private Set<Child> trackedChildren = new HashSet<>();

    // --- 2. VERİLEN TAVSİYELER (İlişkinin Karşı Tarafı) ---
    // Burada @JoinTable OLMAZ. Sadece mappedBy olur.
    // ExpertRecommendation sınıfındaki "expert" değişkenine bakacak.
    @OneToMany(mappedBy = "expert", fetch = FetchType.LAZY)
    private List<ExpertRecommendation> recommendations = new ArrayList<>();

    public Expert() {
    }

    // --- Getter ve Setter ---

    public Set<Child> getTrackedChildren() {
        return trackedChildren;
    }

    public void setTrackedChildren(Set<Child> trackedChildren) {
        this.trackedChildren = trackedChildren;
    }

    public List<ExpertRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<ExpertRecommendation> recommendations) {
        this.recommendations = recommendations;
    }
}