package com.fritomix.erp.modules.dispatch.domain.repository;

import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {
    Optional<Dispatch> findByDispatchNumber(String dispatchNumber);
    boolean existsByDispatchNumber(String dispatchNumber);
    @Query("SELECT d FROM Dispatch d " +
           "JOIN d.orders o " +
           "JOIN FETCH d.driver " +
           "JOIN FETCH d.vehicle " +
           "WHERE o.id = :orderId")
    Optional<Dispatch> findByOrderId(Long orderId);

    @Query("SELECT DISTINCT d FROM Dispatch d " +
           "JOIN FETCH d.orders " +
           "JOIN FETCH d.driver " +
           "JOIN FETCH d.vehicle")
    List<Dispatch> findAllWithFetch();

    @Query("SELECT DISTINCT d FROM Dispatch d " +
           "JOIN d.orders o " +
           "JOIN FETCH d.orders " +
           "JOIN FETCH d.driver " +
           "JOIN FETCH d.vehicle " +
           "WHERE o.id = :orderId " +
           "ORDER BY d.dispatchDate DESC")
    List<Dispatch> findAllByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT d FROM Dispatch d " +
           "JOIN d.orders o " +
           "WHERE o.id = :orderId " +
           "AND d.status NOT IN :closedStatuses")
    List<Dispatch> findActiveByOrderId(@Param("orderId") Long orderId,
                                       @Param("closedStatuses") Collection<String> closedStatuses);

    @Query("SELECT DISTINCT d FROM Dispatch d " +
           "JOIN FETCH d.orders " +
           "JOIN FETCH d.driver " +
           "JOIN FETCH d.vehicle " +
           "WHERE d.dispatchDate BETWEEN :desde AND :hasta " +
           "ORDER BY d.dispatchDate DESC")
    List<Dispatch> findAllBetweenDates(@Param("desde") LocalDateTime desde,
                                       @Param("hasta") LocalDateTime hasta);

    @Query("SELECT DISTINCT d FROM Dispatch d " +
           "JOIN FETCH d.orders o " +
           "JOIN FETCH o.customer " +
           "JOIN FETCH d.driver " +
           "JOIN FETCH d.vehicle " +
           "WHERE d.status = :status " +
           "ORDER BY d.dispatchDate DESC")
    List<Dispatch> findAllByStatusWithFetch(@Param("status") String status);
}
