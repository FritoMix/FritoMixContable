package com.fritomix.erp.modules.auth.domain.repository;

import com.fritomix.erp.modules.auth.domain.entity.RefreshToken;
import com.fritomix.erp.modules.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUser(User user);
}
