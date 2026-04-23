package com.otigo.auth_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otigo.auth_api.entity.ExpertParentConnection;
import com.otigo.auth_api.entity.ExpertParentConnection.ConnectionStatus;
import com.otigo.auth_api.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpertParentConnectionRepository extends JpaRepository<ExpertParentConnection, Long> {

    List<ExpertParentConnection> findByExpertAndStatus(UserEntity expert, ConnectionStatus status);

    List<ExpertParentConnection> findByParentAndStatus(UserEntity parent, ConnectionStatus status);

    List<ExpertParentConnection> findByExpert(UserEntity expert);

    List<ExpertParentConnection> findByParent(UserEntity parent);

    Optional<ExpertParentConnection> findByExpertAndParent(UserEntity expert, UserEntity parent);
}