package com.fritomix.erp.modules.camaras.domain.entity;

import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "camara_verificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CamaraVerificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", nullable = false)
    private Dispatch dispatch;

    @Column(name = "estado_camaras", nullable = false, length = 30)
    @Builder.Default
    private String estadoCamaras = "PENDIENTE_CAMARAS";

    @Column(name = "verificado_por_user_id")
    private Long verificadoPorUserId;

    @Column(name = "aprobado_por_user_id")
    private Long aprobadoPorUserId;

    @Column(name = "fecha_inicio_verificacion")
    private LocalDateTime fechaInicioVerificacion;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "observaciones_generales", columnDefinition = "TEXT")
    private String observacionesGenerales;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "verificacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CamaraVerificacionDetalle> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "verificacion", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CamaraHistorial> historial = new ArrayList<>();

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
