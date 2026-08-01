package com.fritomix.erp.modules.customers.domain.repository;

import com.fritomix.erp.modules.customers.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByDocument(String document);
    boolean existsByDocument(String document);
    Optional<Customer> findByCode(String code);
    boolean existsByCode(String code);
}
