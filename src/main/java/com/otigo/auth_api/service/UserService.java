package com.otigo.auth_api.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.entity.enums.AccountStatus;
import com.otigo.auth_api.entity.enums.UserRole;
import com.otigo.auth_api.repository.ActivityRepository;
import com.otigo.auth_api.repository.ActivityResultRepository;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.ExpertParentConnectionRepository;
import com.otigo.auth_api.repository.ExpertRecommendationRepository;
import com.otigo.auth_api.repository.FcmTokenRepository;
import com.otigo.auth_api.repository.MessageRepository;
import com.otigo.auth_api.repository.SymptomSurveyRepository;
import com.otigo.auth_api.repository.UserRepository;
import com.otigo.auth_api.token.PasswordResetTokenRepository;
import com.otigo.auth_api.token.VerificationTokenRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChildRepository childRepository;
    private final ActivityRepository activityRepository;
    private final ActivityResultRepository activityResultRepository;
    private final SymptomSurveyRepository symptomSurveyRepository;
    private final ExpertRecommendationRepository expertRecommendationRepository;
    private final ExpertParentConnectionRepository expertParentConnectionRepository;
    private final MessageRepository messageRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ChildRepository childRepository,
                       ActivityRepository activityRepository,
                       ActivityResultRepository activityResultRepository,
                       SymptomSurveyRepository symptomSurveyRepository,
                       ExpertRecommendationRepository expertRecommendationRepository,
                       ExpertParentConnectionRepository expertParentConnectionRepository,
                       MessageRepository messageRepository,
                       FcmTokenRepository fcmTokenRepository,
                       VerificationTokenRepository verificationTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.childRepository = childRepository;
        this.activityRepository = activityRepository;
        this.activityResultRepository = activityResultRepository;
        this.symptomSurveyRepository = symptomSurveyRepository;
        this.expertRecommendationRepository = expertRecommendationRepository;
        this.expertParentConnectionRepository = expertParentConnectionRepository;
        this.messageRepository = messageRepository;
        this.fcmTokenRepository = fcmTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public void deactivateAccount(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));
        user.setStatus(AccountStatus.FROZEN);
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));

        fcmTokenRepository.findByUser(user).ifPresent(fcmTokenRepository::delete);
        verificationTokenRepository.findByUser(user).ifPresent(verificationTokenRepository::delete);
        passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);

        messageRepository.findBySenderIdOrReceiverId(user.getId(), user.getId())
                .forEach(messageRepository::delete);

        expertParentConnectionRepository.findByParent(user).forEach(expertParentConnectionRepository::delete);
        expertParentConnectionRepository.findByExpert(user).forEach(expertParentConnectionRepository::delete);

        List<Child> children = childRepository.findByParent(user);
        for (Child child : children) {
            symptomSurveyRepository.findByChildOrderBySurveyDateDesc(child)
                    .forEach(symptomSurveyRepository::delete);
            expertRecommendationRepository.findByChildOrderByCreatedAtDesc(child)
                    .forEach(expertRecommendationRepository::delete);
            activityResultRepository.findByChildOrderByPlayedAtDesc(child)
                    .forEach(activityResultRepository::delete);
            activityRepository.findByChildId(child.getId())
                    .forEach(activityRepository::delete);
            childRepository.delete(child);
        }

        symptomSurveyRepository.findByParentOrderBySurveyDateDesc(user)
                .forEach(symptomSurveyRepository::delete);

        userRepository.delete(user);
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Mevcut şifre yanlış.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void updateProfilePhoto(UserEntity user, String photoBase64) {
        user.setProfilePhoto(photoBase64);
        userRepository.save(user);
    }

    @Transactional
    public void updateProfile(UserEntity user, String phoneNumber, String address) {
        if (phoneNumber != null) user.setPhoneNumber(phoneNumber);

        // Adres sadece uzman için
        if (address != null && user.getRole() == UserRole.UZMAN) {
            user.setAddress(address);
        }

        userRepository.save(user);
    }
}