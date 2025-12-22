package com.otigo.auth_api.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.entity.enums.AccountStatus;
import com.otigo.auth_api.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Bir kullanıcının hesabını DONDURUR.
     * @param email Hesabı dondurulacak kullanıcının e-postası
     */
    public void deactivateAccount(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));

        // Durumu DEACTIVATED yerine FROZEN olarak güncelle
        user.setStatus(AccountStatus.FROZEN); 
        userRepository.save(user);
    }
    
    /**
     * Bir kullanıcının hesabını "Soft Delete" (Güvenli Silme) yöntemiyle SİLER.
     * @param email Hesabı silinecek kullanıcının e-postası
     */
    public void deleteAccount(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));

        // 1. Durumu DELETED olarak güncelle
        user.setStatus(AccountStatus.DELETED);
        
        // 2. Kişisel verileri anonimleştir
        user.setEmail(user.getId() + "@deleted.user"); 
        user.setPassword(""); // Parolayı temizle
        // (Varsa diğer kişisel verileri de temizle)
        
        // 3. Anonimleştirilmiş kullanıcıyı kaydet
        userRepository.save(user);
    }
}