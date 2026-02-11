package com.collabnex.domain.vendor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendor_team")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(length = 100)
    private String role;   // Designer, QA, Engineer

    @Column(name = "count")
    private Integer count;
}
