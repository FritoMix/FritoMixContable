package com.fritomix.erp.modules.dispatch.domain.repository;

import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = """
            SELECT d.id FROM Dispatch d
            WHERE (:search IS NULL
                   OR LOWER(d.dispatchNumber) LIKE LOWER(CAST(:search AS string))
                   OR LOWER(d.driver.name) LIKE LOWER(CAST(:search AS string))
                   OR LOWER(d.vehicle.vehicleNumber) LIKE LOWER(CAST(:search AS string))
                   OR EXISTS (SELECT o FROM d.orders o
                              WHERE LOWER(o.orderNumber) LIKE LOWER(CAST(:search AS string))))
            ORDER BY d.dispatchDate DESC
            """,
           countQuery = """
            SELECT COUNT(d) FROM Dispatch d
            WHERE (:search IS NULL
                   OR LOWER(d.dispatchNumber) LIKE LOWER(CAST(:search AS string))
                   OR LOWER(d.driver.name) LIKE LOWER(CAST(:search AS string))
                   OR LOWER(d.vehicle.vehicleNumber) LIKE LOWER(CAST(:search AS string))
                   OR EXISTS (SELECT o FROM d.orders o
                              WHERE LOWER(o.orderNumber) LIKE LOWER(CAST(:search AS string))))
            """)
    Page<Long> findIds(@Param("search") String search, Pageable pageable);

    @Query("SELECT DISTINCT d FROM Dispatch d " +
           "JOIN FETCH d.orders o " +
           "JOIN FETCH o.customer " +
           "JOIN FETCH d.driver " +
           "JOIN FETCH d.vehicle " +
           "WHERE d.id IN :ids " +
           "ORDER BY d.dispatchDate DESC")
    List<Dispatch> findAllWithFetchByIds(@Param("ids") Collection<Long> ids);

    @Query("SELECT DISTINCT d FROM Dispatch d " +
           "JOIN d.orders o " +
           "JOIN FETCH d.orders " +
           "JOIN FETCH d.driver " +
           "JOIN FETCH d.vehicle " +
           "WHERE o.id = :orderId " +
           "ORDER BY d.dispatchDate DESC")
    List<Dispatch> findAllByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT DISTINCT d FROM Dispatch d " +
           "JOIN FETCH d.orders " +
           "JOIN FETCH d.driver " +
           "JOIN FETCH d.vehicle " +
           "WHERE d.dispatchDate BETWEEN :desde AND :hasta " +
           "ORDER BY d.dispatchDate DESC")
    List<Dispatch> findAllBetweenDates(@Param("desde") LocalDateTime desde,
                                       @Param("hasta") LocalDateTime hasta);

    @Query(value = """
            SELECT d.id FROM Dispatch d
            WHERE d.dispatchDate BETWEEN :desde AND :hasta
            ORDER BY d.dispatchDate DESC
            """,
           countQuery = "SELECT COUNT(d) FROM Dispatch d WHERE d.dispatchDate BETWEEN :desde AND :hasta")
    Page<Long> findIdsBetweenDates(@Param("desde") LocalDateTime desde,
                                   @Param("hasta") LocalDateTime hasta,
                                   Pageable pageable);

    @Query("SELECT DISTINCT d FROM Dispatch d " +
           "JOIN FETCH d.orders o " +
           "JOIN FETCH o.customer " +
           "JOIN FETCH d.driver " +
           "JOIN FETCH d.vehicle " +
           "WHERE d.status = :status " +
           "ORDER BY d.dispatchDate DESC")
    List<Dispatch> findAllByStatusWithFetch(@Param("status") String status);
}
