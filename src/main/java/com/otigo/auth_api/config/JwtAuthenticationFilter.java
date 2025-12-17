package com.otigo.auth_api.config;

import com.otigo.auth_api.user.UserEntity; // Kendi User varlığımız
import com.otigo.auth_api.user.UserRepository; // SARI UYARIYI VEREN ALAN (ŞİMDİ KULLANILACAK)
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
// UserDetailsService import'u kaldırıldı, artık gerek yok
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections; // Authorities (roller) için eklendi

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository; // Artık UserDetailsService'e gerek yok

    // Constructor güncellendi
    public JwtAuthenticationFilter(JwtService jwtService,
                                     UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractEmail(jwt);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        // 6. E-posta var VE kullanıcı henüz "giriş yapmış" olarak işaretlenmemişse
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 7. --- DÜZELTME BURADA ---
            // userDetailsService'i kullanmak yerine, e-postaya göre 
            // KENDİ User varlığımızı userRepository'den buluyoruz.
            UserEntity user = this.userRepository.findByEmail(userEmail)
                    .orElse(null); // Kullanıcı bulunamazsa null döner

            // 8. Token'ın geçerliliğini KENDİ user nesnemizle kontrol et
            if (user != null && jwtService.isTokenValid(jwt, user)) { // Artık doğru nesneyi kullanıyor
                
                // 9. Token geçerliyse, Spring Security için bir Authentication nesnesi oluştur
                // Principal (kimlik) olarak da kendi User nesnemizi atıyoruz
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, // Kendi User nesnemiz
                        null,
                        Collections.emptyList() // TODO: Roller eklenecek (user.getRole() ile)
                );
                
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 10. Spring Security'nin Context'ine bu kullanıcıyı ata
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}