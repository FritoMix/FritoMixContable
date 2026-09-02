package com.fritomix.erp.modules.auth.domain.repository;

import com.fritomix.erp.modules.auth.domain.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findFirstByEmailAndUsedFalseOrderByIdDesc(String email);

    void deleteByEmail(String email);
}
