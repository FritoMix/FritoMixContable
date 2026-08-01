package com.fritomix.erp.modules.settings.domain.repository;

import com.fritomix.erp.modules.settings.domain.entity.CompanySetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanySettingRepository extends JpaRepository<CompanySetting, Long> {
}
