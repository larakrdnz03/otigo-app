package com.otigo.auth_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.ExpertParentConnection;
import com.otigo.auth_api.entity.ExpertParentConnection.ConnectionStatus;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.entity.enums.UserRole;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.ExpertParentConnectionRepository;
import com.otigo.auth_api.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpertParentConnectionService {

    private final ExpertParentConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final ChildRepository childRepository;

    public ExpertParentConnectionService(
            ExpertParentConnectionRepository connectionRepository,
            UserRepository userRepository,
            ChildRepository childRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.childRepository = childRepository;
    }

    /**
     * Veli, uzmanın emailini girerek bağlantı isteği gönderir.
     */
    @Transactional
    public ExpertParentConnection sendRequest(UserEntity parent, String expertEmail) {

        UserEntity expert = userRepository.findByEmail(expertEmail)
                .orElseThrow(() -> new RuntimeException("Bu emaile sahip kullanıcı bulunamadı."));

        if (expert.getRole() != UserRole.UZMAN) {
            throw new RuntimeException("Bu kullanıcı bir uzman değil.");
        }

        if (expert.getId().equals(parent.getId())) {
            throw new RuntimeException("Kendinize istek gönderemezsiniz.");
        }

        connectionRepository.findByExpertAndParent(expert, parent).ifPresent(c -> {
            throw new RuntimeException("Bu uzmana zaten bir istek gönderilmiş.");
        });

        ExpertParentConnection connection = new ExpertParentConnection();
        connection.setExpert(expert);
        connection.setParent(parent);
        connection.setStatus(ConnectionStatus.PENDING);
        connection.setCreatedAt(LocalDateTime.now());

        return connectionRepository.save(connection);
    }

    /**
     * Uzman bekleyen istekleri görür.
     */
    public List<ExpertParentConnection> getPendingRequests(UserEntity expert) {
        return connectionRepository.findByExpertAndStatus(expert, ConnectionStatus.PENDING);
    }

    /**
     * Uzman isteği kabul eder.
     */
    @Transactional
    public ExpertParentConnection acceptRequest(UserEntity expert, Long connectionId) {
        ExpertParentConnection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Bağlantı isteği bulunamadı."));

        if (!connection.getExpert().getId().equals(expert.getId())) {
            throw new RuntimeException("Bu isteği kabul etme yetkiniz yok.");
        }

        connection.setStatus(ConnectionStatus.ACCEPTED);
        connection.setUpdatedAt(LocalDateTime.now());
        return connectionRepository.save(connection);
    }

    /**
     * Uzman isteği reddeder.
     */
    @Transactional
    public ExpertParentConnection rejectRequest(UserEntity expert, Long connectionId) {
        ExpertParentConnection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Bağlantı isteği bulunamadı."));

        if (!connection.getExpert().getId().equals(expert.getId())) {
            throw new RuntimeException("Bu isteği reddetme yetkiniz yok.");
        }

        connection.setStatus(ConnectionStatus.REJECTED);
        connection.setUpdatedAt(LocalDateTime.now());
        return connectionRepository.save(connection);
    }

    /**
     * Velinin bağlı uzmanlarını getirir.
     */
    public List<ExpertParentConnection> getMyExperts(UserEntity parent) {
        return connectionRepository.findByParentAndStatus(parent, ConnectionStatus.ACCEPTED);
    }

    /**
     * Uzmanın bağlı velilerini ve çocuklarını getirir.
     */
    public List<ExpertParentConnection> getMyParents(UserEntity expert) {
        return connectionRepository.findByExpertAndStatus(expert, ConnectionStatus.ACCEPTED);
    }

    /**
     * Uzmanın takip ettiği çocukları getirir.
     */
    public List<Child> getMyChildren(UserEntity expert) {
        List<ExpertParentConnection> connections = connectionRepository
                .findByExpertAndStatus(expert, ConnectionStatus.ACCEPTED);

        return connections.stream()
                .flatMap(c -> childRepository.findByParent(c.getParent()).stream())
                .toList();
    }
}