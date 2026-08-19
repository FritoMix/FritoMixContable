package com.fritomix.erp.modules.vehicles.domain.repository;

import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
    boolean existsByVehicleNumber(String vehicleNumber);

    @Query("""
            SELECT v FROM Vehicle v
            WHERE (:search IS NULL
                   OR LOWER(v.vehicleNumber) LIKE LOWER(CAST(:search AS string))
                   OR LOWER(v.type) LIKE LOWER(CAST(:search AS string)))
            """)
    Page<Vehicle> search(@Param("search") String search, Pageable pageable);
}
