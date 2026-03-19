package com.collabnex.domain.job;

import com.collabnex.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a job posting by a client.
 * Jobs are visible to freelancers and can receive applications.
 * The {@code isPaidClient} flag is copied from the posting client's {@code isPaidMember}
 * field at creation time so paid clients' jobs sort higher in feeds.
 */
@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Column(name = "is_paid_client")
    private boolean isPaidClient;

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
