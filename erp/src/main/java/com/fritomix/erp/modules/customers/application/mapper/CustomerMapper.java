package com.fritomix.erp.modules.customers.application.mapper;

import com.fritomix.erp.modules.customers.application.dto.response.CustomerResponse;
import com.fritomix.erp.modules.customers.domain.entity.Customer;
import com.fritomix.erp.modules.customers.domain.entity.CustomerAddress;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        return toResponse(customer, null);
    }

    public CustomerResponse toResponse(Customer customer, CustomerAddress address) {
        Long cityId = null;
        String cityName = null;
        Long departmentId = null;
        String departmentName = null;
        if (address != null && address.getCity() != null) {
            cityId = address.getCity().getId();
            cityName = address.getCity().getName();
            if (address.getCity().getDepartment() != null) {
                departmentId = address.getCity().getDepartment().getId();
                departmentName = address.getCity().getDepartment().getName();
            }
        }
        return CustomerResponse.builder()
                .id(customer.getId())
                .code(customer.getCode())
                .document(customer.getDocument())
                .businessName(customer.getBusinessName())
                .contactName(customer.getContactName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .active(customer.getActive())
                .address(customer.getAddress())
                .cityId(cityId)
                .cityName(cityName)
                .departmentId(departmentId)
                .departmentName(departmentName)
                .createdAt(customer.getCreatedAt())
                .build();
    }
}
