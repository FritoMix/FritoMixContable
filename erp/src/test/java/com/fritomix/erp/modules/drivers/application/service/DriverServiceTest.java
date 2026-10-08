package com.fritomix.erp.modules.drivers.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.drivers.application.dto.request.DriverRequest;
import com.fritomix.erp.modules.drivers.application.dto.response.DriverResponse;
import com.fritomix.erp.modules.drivers.application.mapper.DriverMapper;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.drivers.domain.repository.DriverRepository;
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
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Spy
    private DriverMapper mapper = new DriverMapper();

    @InjectMocks
    private DriverService service;

    private Driver driver(long id) {
        return Driver.builder()
                .id(id).document("CC" + id).name("Conductor " + id)
                .phone("300" + id).licenseNumber("LIC" + id).active(true)
                .build();
    }

    @Test
    void findAllConBusquedaEnviaPatronLike() {
        Pageable pageable = PageRequest.of(0, 10);
        when(driverRepository.search("%ped%", pageable))
                .thenReturn(new PageImpl<>(List.of(driver(1L)), pageable, 1));

        PageResponse<DriverResponse> result = service.findAll("  ped  ", pageable);

        assertEquals(1, result.content().size());
        assertEquals("CC1", result.content().get(0).document());
        assertEquals(1, result.totalElements());
    }

    @Test
    void findAllSinBusquedaUsaNull() {
        Pageable pageable = PageRequest.of(0, 10);
        when(driverRepository.search(null, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        PageResponse<DriverResponse> result = service.findAll("   ", pageable);

        assertTrue(result.content().isEmpty());
    }

    @Test
    void findByIdEncontrado() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver(1L)));

        assertEquals("Conductor 1", service.findById(1L).name());
    }

    @Test
    void findByIdNoEncontradoLanzaExcepcion() {
        when(driverRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }

    @Test
    void createGuardaConActivoPorDefecto() {
        when(driverRepository.existsByDocument("CC1")).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenAnswer(inv -> inv.getArgument(0));

        DriverResponse response = service.create(DriverRequest.builder()
                .document("CC1").name("Pedro").licenseNumber("LIC1").build());

        assertEquals("CC1", response.document());
        assertEquals("Pedro", response.name());
        assertEquals(Boolean.TRUE, response.active());
    }

    @Test
    void createConDocumentoDuplicadoLanzaExcepcion() {
        when(driverRepository.existsByDocument("CC1")).thenReturn(true);

        DriverRequest request = DriverRequest.builder()
                .document("CC1").name("Pedro").licenseNumber("LIC1").build();

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
    }

    @Test
    void updateCambiaCamposYDocumentoSinConflicto() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver(1L)));
        when(driverRepository.existsByDocument("CC9")).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenAnswer(inv -> inv.getArgument(0));

        DriverResponse response = service.update(1L, DriverRequest.builder()
                .document("CC9").name("Pedro Nuevo").phone("311").licenseNumber("LIC9").active(false).build());

        assertEquals("CC9", response.document());
        assertEquals("Pedro Nuevo", response.name());
        assertEquals("311", response.phone());
        assertEquals("LIC9", response.licenseNumber());
        assertEquals(Boolean.FALSE, response.active());
    }

    @Test
    void updateConDocumentoDuplicadoLanzaExcepcion() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver(1L)));
        when(driverRepository.existsByDocument("CC9")).thenReturn(true);

        DriverRequest request = DriverRequest.builder()
                .document("CC9").name("X").licenseNumber("L").build();

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, request));
    }

    @Test
    void updateNoEncontradoLanzaExcepcion() {
        when(driverRepository.findById(9L)).thenReturn(Optional.empty());

        DriverRequest request = DriverRequest.builder()
                .document("X").name("X").licenseNumber("L").build();

        assertThrows(ResourceNotFoundException.class, () -> service.update(9L, request));
    }

    @Test
    void deleteEliminaSiExiste() {
        when(driverRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(driverRepository).deleteById(1L);
    }

    @Test
    void deleteNoEncontradoLanzaExcepcion() {
        when(driverRepository.existsById(9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(9L));
    }
}