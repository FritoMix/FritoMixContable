package com.fritomix.erp.modules.multipedidos.domain.entity;

import com.fritomix.erp.modules.orders.domain.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "multipedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Multipedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", nullable = false, unique = true, length = 30)
    private String numero;

    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private String status = "PENDIENTE";

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "multipedido_orders",
        joinColumns = @JoinColumn(name = "multipedido_id"),
        inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
