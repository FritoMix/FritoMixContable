package com.fritomix.erp.modules.dispatch.domain.entity;

import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dispatches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_pedido", length = 20)
    @Builder.Default
    private String tipoPedido = "pedido_unico";

    @ManyToMany
    @JoinTable(
            name = "dispatch_orders",
            joinColumns = @JoinColumn(name = "dispatch_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "dispatch_number", nullable = false, unique = true, length = 50)
    private String dispatchNumber;

    @Column(name = "dispatch_date", nullable = false)
    private LocalDateTime dispatchDate;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(length = 20)
    private String cumplimiento;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "user_id")
    private Long userId;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "dispatch", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DispatchDetail> details = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dispatchDate == null) dispatchDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
