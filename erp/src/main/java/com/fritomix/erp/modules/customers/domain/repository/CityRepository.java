package com.fritomix.erp.modules.customers.domain.repository;

import com.fritomix.erp.modules.customers.domain.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByDepartmentIdOrderByName(Long departmentId);
}
