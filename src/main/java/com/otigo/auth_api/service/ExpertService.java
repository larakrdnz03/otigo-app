package com.otigo.auth_api.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.otigo.auth_api.dto.request.CreateObservationRequest;
import com.otigo.auth_api.dto.request.CreateRecommendationRequest;
import com.otigo.auth_api.entity.Activity;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.Expert;
import com.otigo.auth_api.entity.ExpertRecommendation;
//import com.otigo.auth_api.entity.Observation;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.entity.enums.UserRole;
import com.otigo.auth_api.repository.ActivityRepository;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.ExpertRecommendationRepository;
//import com.otigo.auth_api.repository.ObservationRepository;
import com.otigo.auth_api.repository.UserRepository;

import java.util.Set;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpertService {

    private final UserRepository userRepository;
    private final ChildRepository childRepository;
   //private final ObservationRepository observationRepository;
    private final ExpertRecommendationRepository recommendationRepository;
    private final ActivityRepository activityRepository;

    // Constructor Injection
    public ExpertService(UserRepository userRepository, 
                         ChildRepository childRepository,
                         //ObservationRepository observationRepository,
                         ExpertRecommendationRepository recommendationRepository,
                         ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.childRepository = childRepository;
        //this.observationRepository = observationRepository;
        this.recommendationRepository = recommendationRepository;
        this.activityRepository = activityRepository;
    }

    // --- ÇOCUK TAKİBİ İŞLEMLERİ ---

    @Transactional(readOnly = true)
    public Set<Child> getTrackedChildren(UserEntity user) {
        // Parametre gelen user entity'si bazen proxy olabilir, veritabanından taze çekmek gerekebilir
        // Ancak genelde Controller'dan gelen user eğer Expert ise cast edilebilir.
        if (user instanceof Expert) {
            return ((Expert) user).getTrackedChildren();
        }
        throw new AccessDeniedException("Bu kullanıcı bir Uzman değil, çocuk listesi yok.");
    }

    @Transactional
    public void trackChild(UserEntity user, Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));
        
        // Kullanıcıyı veritabanından taze çekiyoruz
        UserEntity freshUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (freshUser instanceof Expert) {
            Expert expert = (Expert) freshUser;
            expert.getTrackedChildren().add(child);
            userRepository.save(expert);
        } else {
            throw new AccessDeniedException("Sadece Uzmanlar çocuk takip edebilir.");
        }
    }
    
    // --- GÖZLEM EKLEME ---
    /*@Transactional
    public Observation addObservation(UserEntity user, Long childId, CreateObservationRequest request) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));
        
        UserEntity freshUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        if (freshUser instanceof Expert) {
            Expert expert = (Expert) freshUser;
            
            // Uzman bu çocuğu takip ediyor mu kontrolü
            if (!expert.getTrackedChildren().contains(child)) {
                throw new AccessDeniedException("Bu çocuk için gözlem ekleme yetkiniz yok (Takip etmiyorsunuz).");
            }

            Observation newObservation = new Observation();
            newObservation.setNotes(request.getNotes());
            
            // Aktivite kontrolü
            if (request.getActivityId() != null) {
                Activity activity = activityRepository.findById(request.getActivityId())
                        .orElseThrow(() -> new RuntimeException("Seçilen aktivite bulunamadı ID: " + request.getActivityId()));
                newObservation.setActivity(activity);
            }

            newObservation.setObservationDate(
                request.getObservationDate() != null ? request.getObservationDate() : LocalDateTime.now()
            );
            newObservation.setChild(child);
            newObservation.setExpert(expert); // Cast edilmiş expert nesnesini set ettik
            
            return observationRepository.save(newObservation);

        } else {
             throw new AccessDeniedException("Sadece Uzmanlar gözlem ekleyebilir.");
        }
    }*/

    /*@Transactional(readOnly = true)
    public List<Observation> getObservationsForChild(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));
        return observationRepository.findByChildOrderByObservationDateDesc(child);
    }*/

    // --- UZMAN YORUMU / GÖREVİ EKLEME ---
    @Transactional
    public ExpertRecommendation addRecommendation(UserEntity user, Long childId, CreateRecommendationRequest request) {
        
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));

        UserEntity freshUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        if (freshUser instanceof Expert) {
            Expert expert = (Expert) freshUser;

            if (!expert.getTrackedChildren().contains(child)) {
                throw new AccessDeniedException("Takip etmediğiniz bir çocuk için yorum ekleyemezsiniz.");
            }
            
            // Rol kontrolü (instanceof Expert zaten bunu büyük ölçüde garanti eder ama çift dikiş sağlamdır)
            if (expert.getRole() != UserRole.UZMAN) {
                throw new AccessDeniedException("Sadece 'UZMAN' rolündeki kullanıcılar yorum ekleyebilir.");
            }

            // Burada Entity'deki boş constructor kullanılır
            ExpertRecommendation newRecommendation = new ExpertRecommendation(); 
            newRecommendation.setChild(child);
            newRecommendation.setExpert(expert); // Cast edilmiş expert nesnesini set ettik
            newRecommendation.setRecommendationText(request.getRecommendationText());
            
            // Aktivite kontrolü
            if (request.getActivityId() != null) {
                Activity activity = activityRepository.findById(request.getActivityId())
                        .orElseThrow(() -> new RuntimeException("Seçilen aktivite bulunamadı ID: " + request.getActivityId()));
                
                newRecommendation.setActivity(activity);
                
                if (request.getTargetLevel() != null) {
                    newRecommendation.setTargetLevel(request.getTargetLevel());
                }
            }
            
            newRecommendation.setCreatedAt(LocalDateTime.now());

            return recommendationRepository.save(newRecommendation);

        } else {
            throw new AccessDeniedException("Bu işlem sadece Uzmanlar içindir.");
        }
    }

    @Transactional(readOnly = true)
    public List<ExpertRecommendation> getRecommendationsForChild(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));
        
        return recommendationRepository.findByChildOrderByCreatedAtDesc(child);
    }
}