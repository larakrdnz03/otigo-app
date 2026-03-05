package com.otigo.auth_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.UserRepository;

import java.util.List;

@Service
public class ChildService {

    private final ChildRepository childRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;

    public ChildService(ChildRepository childRepository,
                        UserRepository userRepository,
                        ActivityService activityService) {
        this.childRepository = childRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    /**
     * Veliye yeni çocuk ekler ve tüm oyunları otomatik oluşturur.
     */
    @Transactional
    public Child addChild(UserEntity parent, String name, int age) {

        // Velinin taze halini veritabanından çek
        UserEntity freshParent = userRepository.findById(parent.getId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        // Çocuğu oluştur
        Child child = new Child();
        child.setName(name);
        child.setAge(age);
        child.setParent(freshParent);
        Child savedChild = childRepository.save(child);

        // Tüm oyunları ve etkinlikleri otomatik oluştur (ActivityService)
        activityService.createInitialActivitiesForChild(savedChild);

        return savedChild;
    }

    /**
     * Velinin tüm çocuklarını getirir.
     */
    @Transactional(readOnly = true)
    public List<Child> getChildrenForParent(UserEntity parent) {
        UserEntity freshParent = userRepository.findById(parent.getId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        return childRepository.findByParent(freshParent);
    }
}