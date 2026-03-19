package com.collabnex.domain.job;

import com.collabnex.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a freelancer who has been shortlisted by an admin for a specific job.
 * A unique constraint on (job_id, freelancer_id) prevents the same freelancer from being
 * shortlisted twice for the same job.
 */
@Entity
@Table(name = "shortlisted_candidates",
       uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "freelancer_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortlistedCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;

    @Column(name = "selected_by_admin")
    private boolean selectedByAdmin = true;

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
