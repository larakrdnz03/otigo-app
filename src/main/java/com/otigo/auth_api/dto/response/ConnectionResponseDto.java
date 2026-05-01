package com.otigo.auth_api.dto.response;

import com.otigo.auth_api.entity.ExpertParentConnection;
import java.time.LocalDateTime;

public class ConnectionResponseDto {

    private Long id;
    private String status;
    private String expertEmail;
    private String expertName;
    private String parentEmail;
    private String parentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ConnectionResponseDto() {}

    public static ConnectionResponseDto from(ExpertParentConnection connection) {
        ConnectionResponseDto dto = new ConnectionResponseDto();
        dto.setId(connection.getId());
        dto.setStatus(connection.getStatus().name());
        dto.setExpertEmail(connection.getExpert().getEmail());
        dto.setExpertName(connection.getExpert().getFirstname() + " " + connection.getExpert().getLastname());
        dto.setParentEmail(connection.getParent().getEmail());
        dto.setParentName(connection.getParent().getFirstname() + " " + connection.getParent().getLastname());
        dto.setCreatedAt(connection.getCreatedAt());
        dto.setUpdatedAt(connection.getUpdatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getExpertEmail() { return expertEmail; }
    public void setExpertEmail(String expertEmail) { this.expertEmail = expertEmail; }

    public String getExpertName() { return expertName; }
    public void setExpertName(String expertName) { this.expertName = expertName; }

    public String getParentEmail() { return parentEmail; }
    public void setParentEmail(String parentEmail) { this.parentEmail = parentEmail; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}