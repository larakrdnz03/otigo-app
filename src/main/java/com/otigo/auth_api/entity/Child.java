package com.otigo.auth_api.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "children")
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;

    @JsonIgnore
    @ManyToMany(mappedBy = "trackedChildren")
    private Set<Expert> experts = new HashSet<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id", nullable = false)
    private UserEntity parent;

    @JsonIgnore
    @OneToMany(mappedBy = "child", fetch = FetchType.LAZY)
    private List<ExpertRecommendation> recommendations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "child", fetch = FetchType.LAZY)
    private List<ActivityResult> activityResults = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "child", fetch = FetchType.LAZY)
    private List<Activity> activities = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "child", fetch = FetchType.LAZY)
    private List<SymptomSurvey> surveys = new ArrayList<>();

    public Child() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Set<Expert> getExperts() { return experts; }
    public void setExperts(Set<Expert> experts) { this.experts = experts; }

    public UserEntity getParent() { return parent; }
    public void setParent(UserEntity parent) { this.parent = parent; }

    public List<ExpertRecommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<ExpertRecommendation> recommendations) { this.recommendations = recommendations; }

    public List<ActivityResult> getActivityResults() { return activityResults; }
    public void setActivityResults(List<ActivityResult> activityResults) { this.activityResults = activityResults; }

    public List<Activity> getActivities() { return activities; }
    public void setActivities(List<Activity> activities) { this.activities = activities; }

    public List<SymptomSurvey> getSurveys() { return surveys; }
    public void setSurveys(List<SymptomSurvey> surveys) { this.surveys = surveys; }
}