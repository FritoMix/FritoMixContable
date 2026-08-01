package com.fritomix.erp.modules.settings.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(length = 50)
    private String nit;

    @Column(name = "admin_email", length = 150)
    private String adminEmail;

    @Column(length = 250)
    private String address;

    @Column(length = 30)
    private String phone;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String department;

    @Column(name = "economic_activity", length = 200)
    private String economicActivity;

    @Column(name = "password_min_length", nullable = false)
    private Integer passwordMinLength;

    @Column(name = "password_require_special", nullable = false)
    private Boolean passwordRequireSpecial;

    @Column(name = "password_expiration_days", nullable = false)
    private Integer passwordExpirationDays;

    @Column(name = "session_timeout_minutes", nullable = false)
    private Integer sessionTimeoutMinutes;

    @Column(name = "max_login_attempts", nullable = false)
    private Integer maxLoginAttempts;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (passwordMinLength == null) passwordMinLength = 8;
        if (passwordRequireSpecial == null) passwordRequireSpecial = true;
        if (passwordExpirationDays == null) passwordExpirationDays = 90;
        if (sessionTimeoutMinutes == null) sessionTimeoutMinutes = 60;
        if (maxLoginAttempts == null) maxLoginAttempts = 5;
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
