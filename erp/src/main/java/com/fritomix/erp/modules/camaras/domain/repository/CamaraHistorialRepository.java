package com.fritomix.erp.modules.camaras.domain.repository;

import com.fritomix.erp.modules.camaras.domain.entity.CamaraHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CamaraHistorialRepository extends JpaRepository<CamaraHistorial, Long> {

    @Query("""
            SELECT h FROM CamaraHistorial h
            WHERE h.verificacion.id = :verificacionId
            ORDER BY h.createdAt ASC
            """)
    List<CamaraHistorial> findByVerificacionIdOrderByCreatedAtAsc(@Param("verificacionId") Long verificacionId);
}
