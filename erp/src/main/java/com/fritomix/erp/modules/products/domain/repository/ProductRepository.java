package com.fritomix.erp.modules.products.domain.repository;

import com.fritomix.erp.modules.products.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);
    boolean existsByCode(String code);

    @Query("""
            SELECT p FROM Product p
            WHERE (:search IS NULL
                   OR LOWER(p.name) LIKE LOWER(CAST(:search AS string))
                   OR LOWER(p.code) LIKE LOWER(CAST(:search AS string)))
            """)
    Page<Product> search(@Param("search") String search, Pageable pageable);
}
