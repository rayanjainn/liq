package com.collabnex.domain.job;

import com.collabnex.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a freelancer's application to a specific job.
 * A unique constraint on (job_id, freelancer_id) prevents duplicate applications.
 */
@Entity
@Table(name = "job_applications",
       uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "freelancer_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Sets the creation timestamp before the entity is first persisted.
     */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
