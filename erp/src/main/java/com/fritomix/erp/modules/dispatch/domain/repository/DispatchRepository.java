package com.fritomix.erp.modules.dispatch.domain.repository;

import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {
    Optional<Dispatch> findByDispatchNumber(String dispatchNumber);
    boolean existsByDispatchNumber(String dispatchNumber);
    @Query("SELECT d FROM Dispatch d " +
           "JOIN FETCH d.order " +
           "JOIN FETCH d.driver " +
           "JOIN FETCH d.vehicle " +
           "WHERE d.order.id = :orderId")
    Optional<Dispatch> findByOrderId(Long orderId);

    @Query("SELECT DISTINCT d FROM Dispatch d " +
           "JOIN FETCH d.order " +
           "JOIN FETCH d.driver " +
           "JOIN FETCH d.vehicle " +
           "LEFT JOIN FETCH d.details")
    List<Dispatch> findAllWithFetch();
}
