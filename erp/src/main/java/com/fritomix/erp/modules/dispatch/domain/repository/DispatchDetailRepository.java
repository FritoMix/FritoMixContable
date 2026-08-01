package com.fritomix.erp.modules.dispatch.domain.repository;

import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispatchDetailRepository extends JpaRepository<DispatchDetail, Long> {
    List<DispatchDetail> findByDispatchId(Long dispatchId);
}
