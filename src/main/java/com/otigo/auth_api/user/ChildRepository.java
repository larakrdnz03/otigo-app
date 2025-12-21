package com.otigo.auth_api.user; // veya com.otigo.auth_api.child

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    // Ebeveyn'e (Parent) göre çocukları bul
    List<Child> findByParent(UserEntity parent);
}