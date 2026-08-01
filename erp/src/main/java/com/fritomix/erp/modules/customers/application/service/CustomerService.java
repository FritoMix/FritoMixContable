package com.fritomix.erp.modules.customers.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.customers.application.dto.request.CustomerRequest;
import com.fritomix.erp.modules.customers.application.dto.response.CustomerResponse;
import com.fritomix.erp.modules.customers.application.mapper.CustomerMapper;
import com.fritomix.erp.modules.customers.domain.entity.City;
import com.fritomix.erp.modules.customers.domain.entity.Customer;
import com.fritomix.erp.modules.customers.domain.entity.CustomerAddress;
import com.fritomix.erp.modules.customers.domain.repository.CityRepository;
import com.fritomix.erp.modules.customers.domain.repository.CustomerAddressRepository;
import com.fritomix.erp.modules.customers.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository addressRepository;
    private final CityRepository cityRepository;
    private final CustomerMapper mapper;

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        List<Customer> customers = customerRepository.findAll();
        List<Long> ids = customers.stream().map(Customer::getId).toList();
        java.util.Map<Long, CustomerAddress> addrMap = new java.util.HashMap<>();
        if (!ids.isEmpty()) {
            addressRepository.findAllMainByCustomerIds(ids)
                    .forEach(a -> addrMap.putIfAbsent(a.getCustomer().getId(), a));
        }
        return customers.stream()
                .map(c -> mapper.toResponse(c, addrMap.get(c.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        CustomerAddress addr = addressRepository
                .findByCustomerIdAndIsMainTrue(id).orElse(null);
        return mapper.toResponse(customer, addr);
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByDocument(request.document())) {
            throw new IllegalArgumentException("Ya existe un cliente con el documento: " + request.document());
        }

        City city = cityRepository.findById(request.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada con id: " + request.cityId()));

        Customer customer = Customer.builder()
                .document(request.document())
                .businessName(request.businessName())
                .contactName(request.contactName())
                .phone(request.phone())
                .email(request.email())
                .address(request.address())
                .active(request.active() != null ? request.active() : true)
                .build();

        customer = customerRepository.save(customer);
        customer.setCode("C-" + customer.getId());
        customer = customerRepository.save(customer);

        CustomerAddress address = CustomerAddress.builder()
                .customer(customer)
                .city(city)
                .isMain(true)
                .build();
        addressRepository.save(address);

        return mapper.toResponse(customer,
                addressRepository.findByCustomerIdAndIsMainTrue(customer.getId()).orElse(null));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        if (request.document() != null && !request.document().equals(customer.getDocument())) {
            if (customerRepository.existsByDocument(request.document())) {
                throw new IllegalArgumentException("Ya existe otro cliente con el documento: " + request.document());
            }
            customer.setDocument(request.document());
        }

        if (request.businessName() != null) customer.setBusinessName(request.businessName());
        if (request.contactName() != null) customer.setContactName(request.contactName());
        if (request.phone() != null) customer.setPhone(request.phone());
        if (request.email() != null) customer.setEmail(request.email());
        if (request.address() != null) customer.setAddress(request.address());
        if (request.active() != null) customer.setActive(request.active());

        customerRepository.save(customer);

        if (request.cityId() != null) {
            City city = cityRepository.findById(request.cityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada con id: " + request.cityId()));

            CustomerAddress address = addressRepository
                    .findByCustomerIdAndIsMainTrue(id).orElse(null);

            if (address == null) {
                address = CustomerAddress.builder()
                        .customer(customer)
                        .city(city)
                        .isMain(true)
                        .build();
            } else {
                address.setCity(city);
            }
            addressRepository.save(address);
        }

        return mapper.toResponse(customer,
                addressRepository.findByCustomerIdAndIsMainTrue(id).orElse(null));
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
