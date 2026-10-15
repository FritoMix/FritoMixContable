package com.fritomix.erp.modules.camaras.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Registro append-only de eventos del ciclo de vida de una verificación de Cámaras.
 * No tiene updated_at porque nunca se modifica, solo se inserta.
 */
@Entity
@Table(name = "camara_historial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CamaraHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verificacion_id", nullable = false)
    private CamaraVerificacion verificacion;

    /** CREADO, INICIADO, VERIFICADO, OBSERVADO, CORREGIDO, APROBADO, RECHAZADO, REABIERTO */
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento;

    @Column(name = "user_id")
    private Long userId;

    /** Desnormalizado para preservar el nombre aunque el usuario sea eliminado */
    @Column(name = "nombre_usuario", length = 200)
    private String nombreUsuario;

    @Column(name = "estado_anterior", length = 30)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", length = 30)
    private String estadoNuevo;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    /** JSON con snapshot de datos relevantes en el momento del evento */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
