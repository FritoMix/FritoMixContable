package com.fritomix.erp.modules.customers.domain.repository;

import com.fritomix.erp.modules.customers.domain.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByDocument(String document);
    boolean existsByDocument(String document);
    Optional<Customer> findByCode(String code);
    boolean existsByCode(String code);

    @Query("""
            SELECT c FROM Customer c
            WHERE (:search IS NULL
                   OR LOWER(c.businessName) LIKE LOWER(CAST(:search AS string))
                   OR LOWER(c.document) LIKE LOWER(CAST(:search AS string)))
            """)
    Page<Customer> search(@Param("search") String search, Pageable pageable);
}
