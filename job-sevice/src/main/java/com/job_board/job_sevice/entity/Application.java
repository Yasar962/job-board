package com.job_board.job_sevice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long jobId;
    private Long candidateId;
    private String candidateEmail;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status; // PENDING, REVIEWED, ACCEPTED, REJECTED
    private LocalDateTime appliedAt;
    @PrePersist
    public void prePersist() {
        this.appliedAt = LocalDateTime.now();
        this.status=ApplicationStatus.PENDING;
    }
}