package com.fritomix.erp.modules.dispatch.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "arrumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arrume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", nullable = false)
    private Dispatch dispatch;

    @Column(name = "num_arrume")
    private Integer numArrume;

    @Column(name = "arrume_producto", length = 255)
    private String arrumeProducto;

    @Column(precision = 18, scale = 2)
    private BigDecimal cantidad;

    @Column(length = 100)
    private String lote;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
