package com.fritomix.erp.modules.notifications.domain.repository;

import com.fritomix.erp.modules.notifications.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Long countByUserIdAndIsReadFalse(Long userId);

    List<Notification> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);
}
