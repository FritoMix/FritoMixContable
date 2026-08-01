package com.fritomix.erp.modules.customers.domain.repository;

import com.fritomix.erp.modules.customers.domain.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
    Optional<CustomerAddress> findByCustomerIdAndIsMainTrue(Long customerId);

    @Query("SELECT a FROM CustomerAddress a WHERE a.customer.id IN :customerIds AND a.isMain = true")
    List<CustomerAddress> findAllMainByCustomerIds(List<Long> customerIds);
}
