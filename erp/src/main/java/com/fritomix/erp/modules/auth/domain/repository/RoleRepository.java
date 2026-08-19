package com.fritomix.erp.modules.auth.domain.repository;

import com.fritomix.erp.modules.auth.domain.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Query("SELECT r.id FROM Role r ORDER BY r.name")
    Page<Long> findIds(Pageable pageable);

    @Query("SELECT DISTINCT r FROM Role r JOIN FETCH r.permissions WHERE r.id IN :ids ORDER BY r.name")
    List<Role> findByIdsWithPermissions(@Param("ids") Collection<Long> ids);
}
