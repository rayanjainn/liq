package com.collabnex.domain.vendor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendor_machineries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorMachinery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false)
    private String machineName;

    private String machineType;
    private String manufacturer;

    private Integer quantity;

    @Column(columnDefinition = "TEXT")
    private String capabilities;

    private Boolean isOperational = true;
}
