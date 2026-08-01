package com.fritomix.erp.modules.vehicles.domain.repository;

import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByPlate(String plate);
    boolean existsByPlate(String plate);
}
