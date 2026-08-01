package com.fritomix.erp.modules.drivers.domain.repository;

import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByDocument(String document);
    boolean existsByDocument(String document);
}
