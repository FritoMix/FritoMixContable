package com.fritomix.erp.modules.camaras.domain.repository;

import com.fritomix.erp.modules.camaras.domain.entity.CamaraVerificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CamaraVerificacionRepository extends JpaRepository<CamaraVerificacion, Long> {

    Optional<CamaraVerificacion> findByDispatchId(Long dispatchId);

    boolean existsByDispatchId(Long dispatchId);

    /** Bandeja paginada con fetch de dispatch y sus órdenes */
    @Query(value = """
            SELECT v.id FROM CamaraVerificacion v
            JOIN v.dispatch d
            WHERE (:estados IS NULL OR v.estadoCamaras IN :estados)
              AND (:search IS NULL
                   OR LOWER(d.dispatchNumber) LIKE LOWER(CAST(:search AS string))
                   OR EXISTS (SELECT o FROM d.orders o
                              WHERE LOWER(o.orderNumber) LIKE LOWER(CAST(:search AS string))
                                 OR LOWER(o.customer.businessName) LIKE LOWER(CAST(:search AS string))))
              AND (CAST(:desde AS LocalDateTime) IS NULL OR v.createdAt >= :desde)
              AND (CAST(:hasta AS LocalDateTime) IS NULL OR v.createdAt <= :hasta)
            ORDER BY v.createdAt DESC
            """,
           countQuery = """
            SELECT COUNT(v) FROM CamaraVerificacion v
            JOIN v.dispatch d
            WHERE (:estados IS NULL OR v.estadoCamaras IN :estados)
              AND (:search IS NULL
                   OR LOWER(d.dispatchNumber) LIKE LOWER(CAST(:search AS string))
                   OR EXISTS (SELECT o FROM d.orders o
                              WHERE LOWER(o.orderNumber) LIKE LOWER(CAST(:search AS string))
                                 OR LOWER(o.customer.businessName) LIKE LOWER(CAST(:search AS string))))
              AND (CAST(:desde AS LocalDateTime) IS NULL OR v.createdAt >= :desde)
              AND (CAST(:hasta AS LocalDateTime) IS NULL OR v.createdAt <= :hasta)
            """)
    Page<Long> findIds(
            @Param("search") String search,
            @Param("estados") List<String> estados,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable);

    /** Carga completa para bandeja */
    @Query("""
            SELECT DISTINCT v FROM CamaraVerificacion v
            JOIN FETCH v.dispatch d
            JOIN FETCH d.orders o
            JOIN FETCH o.customer
            LEFT JOIN FETCH d.driver
            LEFT JOIN FETCH d.vehicle
            LEFT JOIN FETCH v.detalles det
            LEFT JOIN FETCH det.dispatchDetail dd
            LEFT JOIN FETCH dd.product
            WHERE v.id IN :ids
            ORDER BY v.createdAt DESC
            """)
    List<CamaraVerificacion> findAllWithFetchByIds(@Param("ids") List<Long> ids);

    /** Carga completa para detalle individual */
    @Query("""
            SELECT DISTINCT v FROM CamaraVerificacion v
            JOIN FETCH v.dispatch d
            JOIN FETCH d.orders o
            JOIN FETCH o.customer
            LEFT JOIN FETCH d.driver
            LEFT JOIN FETCH d.vehicle
            LEFT JOIN FETCH v.detalles vd
            LEFT JOIN FETCH vd.dispatchDetail vdd
            LEFT JOIN FETCH vdd.product
            WHERE v.id = :id
            """)
    Optional<CamaraVerificacion> findByIdWithFetch(@Param("id") Long id);
}
