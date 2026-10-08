package com.fritomix.erp.modules.vehicles.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.vehicles.application.dto.request.VehicleRequest;
import com.fritomix.erp.modules.vehicles.application.dto.response.VehicleResponse;
import com.fritomix.erp.modules.vehicles.application.mapper.VehicleMapper;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import com.fritomix.erp.modules.vehicles.domain.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Spy
    private VehicleMapper mapper = new VehicleMapper();

    @InjectMocks
    private VehicleService service;

    private Vehicle vehicle(long id) {
        return Vehicle.builder()
                .id(id).vehicleNumber("ABC" + id).type("Camión")
                .capacity(new BigDecimal("10")).dimension(new BigDecimal("5"))
                .active(true).build();
    }

    @Test
    void findAllConBusquedaEnviaPatronLike() {
        Pageable pageable = PageRequest.of(0, 10);
        when(vehicleRepository.search("%abc%", pageable))
                .thenReturn(new PageImpl<>(List.of(vehicle(1L)), pageable, 1));

        var result = service.findAll("  abc  ", pageable);

        assertEquals(1, result.content().size());
        assertEquals("ABC1", result.content().get(0).vehicleNumber());
    }

    @Test
    void findAllSinBusquedaUsaNull() {
        Pageable pageable = PageRequest.of(0, 10);
        when(vehicleRepository.search(null, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        assertTrue(service.findAll(null, pageable).content().isEmpty());
    }

    @Test
    void findByIdEncontrado() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(1L)));

        assertEquals("ABC1", service.findById(1L).vehicleNumber());
    }

    @Test
    void findByIdNoEncontradoLanzaExcepcion() {
        when(vehicleRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }

    @Test
    void createNormalizaPlacaAMayusculas() {
        when(vehicleRepository.existsByVehicleNumber("abc1")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        VehicleResponse response = service.create(VehicleRequest.builder()
                .vehicleNumber("abc1").type("Camión")
                .capacity(new BigDecimal("10")).dimension(new BigDecimal("5")).build());

        assertEquals("ABC1", response.vehicleNumber());
        assertEquals(Boolean.TRUE, response.active());
    }

    @Test
    void createConPlacaDuplicadaLanzaExcepcion() {
        when(vehicleRepository.existsByVehicleNumber("abc1")).thenReturn(true);

        VehicleRequest request = VehicleRequest.builder().vehicleNumber("abc1").build();

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
    }

    @Test
    void updateCambiaCamposYPlaca() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(1L)));
        when(vehicleRepository.existsByVehicleNumber("xyz9")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        VehicleResponse response = service.update(1L, VehicleRequest.builder()
                .vehicleNumber("xyz9").type("Furgón")
                .capacity(new BigDecimal("20")).dimension(new BigDecimal("8")).active(false).build());

        assertEquals("XYZ9", response.vehicleNumber());
        assertEquals("Furgón", response.type());
        assertEquals(Boolean.FALSE, response.active());
    }

    @Test
    void updateMismaPlacaNoVerificaDuplicado() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(1L)));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        VehicleResponse response = service.update(1L, VehicleRequest.builder()
                .vehicleNumber("ABC1").build());

        assertEquals("ABC1", response.vehicleNumber());
    }

    @Test
    void updateConPlacaDuplicadaLanzaExcepcion() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(1L)));
        when(vehicleRepository.existsByVehicleNumber("xyz9")).thenReturn(true);

        VehicleRequest request = VehicleRequest.builder().vehicleNumber("xyz9").build();

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, request));
    }

    @Test
    void updateNoEncontradoLanzaExcepcion() {
        when(vehicleRepository.findById(9L)).thenReturn(Optional.empty());

        VehicleRequest request = VehicleRequest.builder().vehicleNumber("ABC").build();

        assertThrows(ResourceNotFoundException.class, () -> service.update(9L, request));
    }

    @Test
    void deleteEliminaSiExiste() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(vehicleRepository).deleteById(1L);
    }

    @Test
    void deleteNoEncontradoLanzaExcepcion() {
        when(vehicleRepository.existsById(9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(9L));
    }
}