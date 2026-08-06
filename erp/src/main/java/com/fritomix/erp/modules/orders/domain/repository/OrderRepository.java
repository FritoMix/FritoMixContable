package com.fritomix.erp.modules.orders.domain.repository;

import com.fritomix.erp.modules.orders.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    boolean existsByOrderNumber(String orderNumber);
    List<Order> findByStatus(String status);

    @Query("SELECT o FROM Order o WHERE o.status = 'APROBADO' AND NOT EXISTS (SELECT d FROM Dispatch d WHERE o MEMBER OF d.orders)")
    List<Order> findReadyForDispatch();

    @Query("SELECT o.orderNumber FROM Order o WHERE o.orderNumber LIKE 'PED-%'")
    List<String> findAllPedNumbers();
}
