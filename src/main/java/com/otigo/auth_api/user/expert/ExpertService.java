package com.otigo.auth_api.user.expert;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.user.Child;
import com.otigo.auth_api.user.ChildRepository;
import com.otigo.auth_api.user.CreateObservationRequest;
import com.otigo.auth_api.user.CreateRecommendationRequest;
import com.otigo.auth_api.user.Game; // Yeni Import
import com.otigo.auth_api.user.GameRepository; // Yeni Import
import com.otigo.auth_api.user.Observation;
import com.otigo.auth_api.user.ObservationRepository;
import com.otigo.auth_api.user.UserEntity;
import com.otigo.auth_api.user.UserRepository;
import com.otigo.auth_api.user.UserRole;

import java.util.Set;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpertService {

    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final ObservationRepository observationRepository;
    private final ExpertRecommendationRepository recommendationRepository;
    private final GameRepository gameRepository; // <-- YENİ EKLENDİ

    public ExpertService(UserRepository userRepository, 
                         ChildRepository childRepository,
                         ObservationRepository observationRepository,
                         ExpertRecommendationRepository recommendationRepository,
                         GameRepository gameRepository) { // <-- CONSTRUCTOR GÜNCELLENDİ
        this.userRepository = userRepository;
        this.childRepository = childRepository;
        this.observationRepository = observationRepository;
        this.recommendationRepository = recommendationRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true)
    public Set<Child> getTrackedChildren(UserEntity user) {
        if (user instanceof Expert) {
            return ((Expert) user).getTrackedChildren();
        }
        throw new AccessDeniedException("Bu kullanıcı bir Uzman değil, çocuk listesi yok.");
    }

    @Transactional
    public void trackChild(UserEntity user, Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));
        
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
    
    @Transactional
    public Observation addObservation(UserEntity user, Long childId, CreateObservationRequest request) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));
        
        UserEntity freshUser = userRepository.findById(user.getId()).get();

        if (freshUser instanceof Expert) {
            Expert expert = (Expert) freshUser;
            
            if (!expert.getTrackedChildren().contains(child)) {
                throw new AccessDeniedException("Bu çocuk için gözlem ekleme yetkiniz yok.");
            }

            Observation newObservation = new Observation();
            newObservation.setNotes(request.getNotes());
            newObservation.setActivityName(request.getActivityName());
            newObservation.setObservationDate(
                request.getObservationDate() != null ? request.getObservationDate() : LocalDateTime.now()
            );
            newObservation.setChild(child);
            newObservation.setExpert(user);
            
            return observationRepository.save(newObservation);

        } else {
             throw new AccessDeniedException("Sadece Uzmanlar gözlem ekleyebilir.");
        }
    }

    @Transactional(readOnly = true)
    public List<Observation> getObservationsForChild(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));
        return observationRepository.findByChildOrderByObservationDateDesc(child);
    }

    // --- UZMAN YORUMU / GÖREVİ EKLEME ---
    @Transactional
    public ExpertRecommendation addRecommendation(UserEntity user, Long childId, CreateRecommendationRequest request) {
        
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));

        UserEntity freshUser = userRepository.findById(user.getId()).get();

        if (freshUser instanceof Expert) {
            Expert expert = (Expert) freshUser;

            if (!expert.getTrackedChildren().contains(child)) {
                throw new AccessDeniedException("Takip etmediğiniz bir çocuk için yorum ekleyemezsiniz.");
            }
            
            if (expert.getRole() != UserRole.UZMAN) {
                throw new AccessDeniedException("Sadece 'UZMAN' rolündeki kullanıcılar yorum ekleyebilir.");
            }

            ExpertRecommendation newRecommendation = new ExpertRecommendation();
            newRecommendation.setChild(child);
            newRecommendation.setExpert(user);
            newRecommendation.setRecommendationText(request.getRecommendationText());
            
            // --- YENİ EKLENEN KISIM: OYUN ATAMA ---
            // Eğer istekte bir Oyun ID varsa, o oyunu bul ve göreve ekle
            if (request.getGameId() != null) {
                Game game = gameRepository.findById(request.getGameId())
                        .orElseThrow(() -> new RuntimeException("Seçilen oyun bulunamadı ID: " + request.getGameId()));
                newRecommendation.setGame(game);
                
                // Hedef seviye varsa onu da ekle (Yoksa null kalır)
                if (request.getTargetLevel() != null) {
                    newRecommendation.setTargetLevel(request.getTargetLevel());
                }
            }
            
            // Eski recommendationDate yerine createdAt kullanıyoruz
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
        
        // Düzeltildi: CreatedAt'e göre sırala
        return recommendationRepository.findByChildOrderByCreatedAtDesc(child);
    }
}