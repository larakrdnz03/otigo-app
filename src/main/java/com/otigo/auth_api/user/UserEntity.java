package com.otigo.auth_api.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED) // <-- BU SATIR ÇOK ÖNEMLİ
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;


    // 1. Kullanıcının GÖNDERDİĞİ mesajlar
    // Message sınıfındaki "sender" değişkenine bakar.
    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private List<Message> sentMessages = new ArrayList<>();

    // 2. Kullanıcıya GELEN mesajlar
    // Message sınıfındaki "receiver" değişkenine bakar.
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<Message> receivedMessages = new ArrayList<>();

    // 3. Kullanıcının DOLDURDUĞU anketler
    // SymptomSurvey sınıfındaki "filledBy" değişkenine bakar.
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<SymptomSurvey> filledSurveys = new ArrayList<>();

    public UserEntity() {
    }

    // MANUEL GETTER VE SETTERLAR
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public List<Message> getSentMessages() { return sentMessages; }
    public void setSentMessages(List<Message> sentMessages) { this.sentMessages = sentMessages; }

    public List<Message> getReceivedMessages() { return receivedMessages; }
    public void setReceivedMessages(List<Message> receivedMessages) { this.receivedMessages = receivedMessages; }

    public List<SymptomSurvey> getFilledSurveys() { return filledSurveys; }
    public void setFilledSurveys(List<SymptomSurvey> filledSurveys) { this.filledSurveys = filledSurveys; }

    // UserDetails Metotları
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role == null ? "VELI" : role.name()));
    }
    @Override
    public String getUsername() { return email; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}