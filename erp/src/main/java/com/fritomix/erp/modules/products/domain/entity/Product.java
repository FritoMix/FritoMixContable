package com.fritomix.erp.modules.products.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 250)
    private String description;

    @Column(nullable = false, length = 30)
    private String unit;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column
    @Builder.Default
    private Integer presentation = 0;

    @Column(length = 50)
    private String weight;

    @Column(name = "weight_grams")
    @Builder.Default
    private Integer weightGrams = 0;

    @Column(name = "peso_unidad", precision = 18, scale = 2)
    private BigDecimal pesoUnidad;

    @Column(precision = 18, scale = 15)
    private BigDecimal dimension;

    @Column(name = "peso_total_cargue", precision = 18, scale = 4)
    private BigDecimal pesoTotalCargue;

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
