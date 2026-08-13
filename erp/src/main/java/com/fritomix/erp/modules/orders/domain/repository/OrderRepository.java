package com.fritomix.erp.modules.orders.domain.repository;

import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    boolean existsByOrderNumber(String orderNumber);

    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.customer " +
           "JOIN FETCH o.details d " +
           "JOIN FETCH d.product " +
           "WHERE o.status = :status " +
           "ORDER BY o.orderDate DESC")
    List<Order> findByStatusWithDetails(@Param("status") String status);

    @Query("SELECT de FROM OrderDetail de " +
           "JOIN FETCH de.order " +
           "JOIN FETCH de.product " +
           "WHERE de.order.id IN :orderIds")
    List<OrderDetail> findDetailsByOrderIds(@Param("orderIds") Collection<Long> orderIds);

    @Query("SELECT o FROM Order o WHERE o.status = 'APROBADO' AND NOT EXISTS (SELECT d FROM Dispatch d WHERE o MEMBER OF d.orders)")
    List<Order> findReadyForDispatch();

    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(order_number, 5) AS INTEGER)), 0) FROM orders WHERE order_number LIKE 'PED-%'", nativeQuery = true)
    Integer maxOrderNumber();
}
