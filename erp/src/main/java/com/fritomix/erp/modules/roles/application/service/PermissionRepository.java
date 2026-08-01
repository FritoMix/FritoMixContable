package com.fritomix.erp.modules.roles.application.service;

import com.fritomix.erp.modules.auth.domain.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByNameIn(List<String> names);
}
