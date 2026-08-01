package com.fritomix.erp.modules.customers.domain.repository;

import com.fritomix.erp.modules.customers.domain.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
