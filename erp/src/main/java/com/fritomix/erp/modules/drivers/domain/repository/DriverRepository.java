package com.fritomix.erp.modules.drivers.domain.repository;

import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByDocument(String document);
    boolean existsByDocument(String document);

    @Query("""
            SELECT d FROM Driver d
            WHERE (:search IS NULL
                   OR LOWER(d.name) LIKE LOWER(CAST(:search AS string))
                   OR LOWER(d.document) LIKE LOWER(CAST(:search AS string)))
            """)
    Page<Driver> search(@Param("search") String search, Pageable pageable);
}
