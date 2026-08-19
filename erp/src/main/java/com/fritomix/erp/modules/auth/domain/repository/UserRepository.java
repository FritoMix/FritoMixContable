package com.fritomix.erp.modules.auth.domain.repository;

import com.fritomix.erp.modules.auth.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("""
            SELECT u FROM User u JOIN FETCH u.role
            WHERE (:search IS NULL
                   OR LOWER(u.firstName) LIKE LOWER(CAST(:search AS string))
                   OR LOWER(u.lastName) LIKE LOWER(CAST(:search AS string))
                   OR LOWER(u.email) LIKE LOWER(CAST(:search AS string)))
            """)
    Page<User> search(@Param("search") String search, Pageable pageable);
}
