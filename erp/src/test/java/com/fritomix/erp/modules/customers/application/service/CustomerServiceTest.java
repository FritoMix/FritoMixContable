package com.fritomix.erp.modules.customers.application.service;

import com.fritomix.erp.common.dto.PageResponse;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerAddressRepository addressRepository;
    @Mock
    private CityRepository cityRepository;

    @Spy
    private CustomerMapper mapper = new CustomerMapper();

    @InjectMocks
    private CustomerService service;

    private Customer customer(long id) {
        return Customer.builder()
                .id(id).code("C" + id).document("D" + id).businessName("Cliente " + id)
                .contactName("Contacto").phone("300").email("c@x.com").address("Calle 1").active(true)
                .build();
    }

    private City city(long id, String name) {
        return City.builder().id(id).name(name).build();
    }

    private CustomerAddress address(Customer customer, City city) {
        return CustomerAddress.builder().customer(customer).city(city).build();
    }

    @Test
    void findAllMapeaConCiudad() {
        Pageable pageable = PageRequest.of(0, 10);
        Customer c = customer(1L);
        when(customerRepository.search("%cli%", pageable))
                .thenReturn(new PageImpl<>(List.of(c), pageable, 1));
        when(addressRepository.findAllMainByCustomerIds(List.of(1L)))
                .thenReturn(List.of(address(c, city(5L, "Medellín"))));

        PageResponse<CustomerResponse> result = service.findAll("cli", pageable);

        assertEquals(1, result.content().size());
        assertEquals("Cliente 1", result.content().get(0).businessName());
        assertEquals("Medellín", result.content().get(0).cityName());
        assertEquals(5L, result.content().get(0).cityId());
    }

    @Test
    void findAllSinResultadosNoConsultaDirecciones() {
        Pageable pageable = PageRequest.of(0, 10);
        when(customerRepository.search(null, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        assertTrue(service.findAll(null, pageable).content().isEmpty());
    }

    @Test
    void findByIdEncontradoConDireccion() {
        Customer c = customer(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));
        when(addressRepository.findByCustomerIdAndIsMainTrue(1L))
                .thenReturn(Optional.of(address(c, city(5L, "Bogotá"))));

        assertEquals("Bogotá", service.findById(1L).cityName());
    }

    @Test
    void findByIdNoEncontradoLanzaExcepcion() {
        when(customerRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }

    @Test
    void createGeneraCodigoYGuardaDireccion() {
        when(customerRepository.existsByDocument("D1")).thenReturn(false);
        when(cityRepository.findById(5L)).thenReturn(Optional.of(city(5L, "Cali")));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            if (c.getId() == null) c.setId(1L);
            return c;
        });
        when(addressRepository.findByCustomerIdAndIsMainTrue(1L))
                .thenReturn(Optional.of(address(customer(1L), city(5L, "Cali"))));

        CustomerResponse response = service.create(CustomerRequest.builder()
                .document("D1").businessName("Nuevo").cityId(5L).build());

        assertEquals("C-1", response.code());
        assertEquals("Nuevo", response.businessName());
        assertEquals(Boolean.TRUE, response.active());
        assertEquals("Cali", response.cityName());
    }

    @Test
    void createConDocumentoDuplicadoLanzaExcepcion() {
        when(customerRepository.existsByDocument("D1")).thenReturn(true);

        CustomerRequest request = CustomerRequest.builder()
                .document("D1").businessName("X").cityId(5L).build();

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
    }

    @Test
    void createConCiudadInexistenteLanzaExcepcion() {
        when(customerRepository.existsByDocument("D1")).thenReturn(false);
        when(cityRepository.findById(5L)).thenReturn(Optional.empty());

        CustomerRequest request = CustomerRequest.builder()
                .document("D1").businessName("X").cityId(5L).build();

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void updateCambiaCamposYDocumentoYReasignaCiudad() {
        Customer c = customer(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));
        when(customerRepository.existsByDocument("D9")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cityRepository.findById(7L)).thenReturn(Optional.of(city(7L, "Pereira")));
        when(addressRepository.findByCustomerIdAndIsMainTrue(1L))
                .thenReturn(Optional.of(address(c, city(5L, "Cali"))));

        CustomerResponse response = service.update(1L, CustomerRequest.builder()
                .document("D9").businessName("Renombrado").cityId(7L).active(false).build());

        assertEquals("D9", response.document());
        assertEquals("Renombrado", response.businessName());
        assertEquals(Boolean.FALSE, response.active());
        assertEquals("Pereira", response.cityName());
    }

    @Test
    void updateConDocumentoDuplicadoLanzaExcepcion() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer(1L)));
        when(customerRepository.existsByDocument("D9")).thenReturn(true);

        CustomerRequest request = CustomerRequest.builder()
                .document("D9").businessName("X").cityId(5L).build();

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, request));
    }

    @Test
    void updateNoEncontradoLanzaExcepcion() {
        when(customerRepository.findById(9L)).thenReturn(Optional.empty());

        CustomerRequest request = CustomerRequest.builder()
                .document("D").businessName("X").cityId(5L).build();

        assertThrows(ResourceNotFoundException.class, () -> service.update(9L, request));
    }

    @Test
    void deleteEliminaSiExiste() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteNoEncontradoLanzaExcepcion() {
        when(customerRepository.existsById(9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(9L));
    }
}