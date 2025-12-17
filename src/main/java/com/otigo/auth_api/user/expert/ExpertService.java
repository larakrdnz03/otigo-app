package com.otigo.auth_api.user;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpertService {

    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final ObservationRepository observationRepository;
    private final ExpertRecommendationRepository recommendationRepository;

    public ExpertService(UserRepository userRepository, 
                         ChildRepository childRepository,
                         ObservationRepository observationRepository,
                         ExpertRecommendationRepository recommendationRepository) {
        this.userRepository = userRepository;
        this.childRepository = childRepository;
        this.observationRepository = observationRepository;
        this.recommendationRepository = recommendationRepository;
    }

    @Transactional(readOnly = true)
    public Set<Child> getTrackedChildren(UserEntity user) {
        // Gelen User'ı Expert'e dönüştürüyoruz (Cast)
        if (user instanceof Expert) {
            return ((Expert) user).getTrackedChildren();
        }
        throw new AccessDeniedException("Bu kullanıcı bir Uzman değil, çocuk listesi yok.");
    }

    @Transactional
    public void trackChild(UserEntity user, Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));
        
        // Veritabanından güncel kullanıcıyı çekiyoruz
        UserEntity freshUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Kontrol edip Expert'e çeviriyoruz
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

        // Expert dönüşümü ve yetki kontrolü
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
            newObservation.setExpert(user); // Observation entity'si User tipinde expert tutuyorsa burası kalabilir
            
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

    // --- UZMAN YORUMU EKLEME ---
    @Transactional
    public ExpertRecommendation addRecommendation(UserEntity user, Long childId, CreateRecommendationRequest request) {
        
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));

        UserEntity freshUser = userRepository.findById(user.getId()).get();

        // Expert dönüşümü
        if (freshUser instanceof Expert) {
            Expert expert = (Expert) freshUser;

            if (!expert.getTrackedChildren().contains(child)) {
                throw new AccessDeniedException("Takip etmediğiniz bir çocuk için yorum ekleyemezsiniz.");
            }
            
            // Rol kontrolünü zaten "instanceof Expert" ile yapmış olduk ama yine de ekleyelim
            if (expert.getRole() != UserRole.UZMAN) {
                throw new AccessDeniedException("Sadece 'UZMAN' rolündeki kullanıcılar yorum ekleyebilir.");
            }

            ExpertRecommendation newRecommendation = new ExpertRecommendation();
            newRecommendation.setChild(child);
            newRecommendation.setExpert(user); // Entity User alıyorsa user veriyoruz
            newRecommendation.setRecommendationText(request.getRecommendationText());
            //newRecommendation.setRecommendationDate(LocalDateTime.now());
            // Yeni (Doğru):
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
        //return recommendationRepository.findByChildOrderByRecommendationDateDesc(child);
        return recommendationRepository.findByChildOrderByCreatedAtDesc(child);

    }
}