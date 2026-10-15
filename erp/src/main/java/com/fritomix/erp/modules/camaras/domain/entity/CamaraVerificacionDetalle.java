package com.fritomix.erp.modules.camaras.domain.entity;

import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "camara_verificacion_detalles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CamaraVerificacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verificacion_id", nullable = false)
    private CamaraVerificacion verificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_detail_id", nullable = false)
    private DispatchDetail dispatchDetail;

    /** Copia inmutable de la cantidad original al momento de crear la verificación */
    @Column(name = "cantidad_despachada", nullable = false, precision = 18, scale = 2)
    private BigDecimal cantidadDespachada;

    @Column(name = "cantidad_verificada", precision = 18, scale = 2)
    private BigDecimal cantidadVerificada;

    /** OK | DIFERENCIA | PENDIENTE */
    @Column(name = "estado_linea", nullable = false, length = 20)
    @Builder.Default
    private String estadoLinea = "PENDIENTE";

    /** Causa de la diferencia: FALTANTE, SOBRANTE, REFERENCIA_INCORRECTA, DANO, ERROR_DESPACHO, OTRO */
    @Column(name = "tipo_inconsistencia", length = 50)
    private String tipoInconsistencia;

    @Column(name = "observacion_linea", columnDefinition = "TEXT")
    private String observacionLinea;

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

    /** Calcula la diferencia en tiempo de ejecución (no almacenada en BD) */
    public BigDecimal getDiferencia() {
        if (cantidadVerificada == null || cantidadDespachada == null) return null;
        return cantidadVerificada.subtract(cantidadDespachada);
    }
}
