package com.fritomix.erp.modules.roles.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.auth.domain.entity.Permission;
import com.fritomix.erp.modules.auth.domain.entity.Role;
import com.fritomix.erp.modules.auth.domain.repository.RoleRepository;
import com.fritomix.erp.modules.roles.application.dto.request.CreateRoleRequest;
import com.fritomix.erp.modules.roles.application.dto.request.UpdateRoleRequest;
import com.fritomix.erp.modules.roles.application.dto.response.RoleResponse;
import com.fritomix.erp.modules.roles.application.mapper.RoleMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleMapper mapper;

    @InjectMocks
    private RoleService service;

    @Test
    void findAllVacioDevuelvePaginaVacia() {
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Long> ids = new PageImpl<>(List.of(), pageable, 0);
        when(roleRepository.findIds(pageable)).thenReturn(ids);

        PageResponse<RoleResponse> result = service.findAll(pageable);

        assertEquals(0, result.content().size());
    }

    @Test
    void findAllMapeaContenido() {
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Long> ids = new PageImpl<>(List.of(1L), pageable, 1);
        when(roleRepository.findIds(pageable)).thenReturn(ids);
        Role r = Role.builder().id(1L).name("ADMIN").build();
        when(roleRepository.findByIdsWithPermissions(List.of(1L))).thenReturn(List.of(r));
        when(mapper.toResponse(r)).thenReturn(mock(RoleResponse.class));

        PageResponse<RoleResponse> result = service.findAll(pageable);

        assertEquals(1, result.content().size());
    }

    @Test
    void findByIdEncontradoYNoEncontrado() {
        Role r = Role.builder().id(1L).name("ADMIN").build();
        when(roleRepository.findById(1L)).thenReturn(Optional.of(r));
        when(mapper.toResponse(r)).thenReturn(mock(RoleResponse.class));

        assertNotNull(service.findById(1L));

        when(roleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void createExitosoConPermisos() {
        CreateRoleRequest request = CreateRoleRequest.builder().name("CAJERO").description("x").permissions(List.of("p1")).build();
        Permission p = Permission.builder().id(1L).name("p1").build();
        when(permissionRepository.findByNameIn(List.of("p1"))).thenReturn(List.of(p));
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });
        when(mapper.toResponse(any(Role.class))).thenReturn(mock(RoleResponse.class));

        assertNotNull(service.create(request));
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void createSinPermisos() {
        CreateRoleRequest request = CreateRoleRequest.builder().name("CAJERO").build();
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });
        when(mapper.toResponse(any(Role.class))).thenReturn(mock(RoleResponse.class));

        assertNotNull(service.create(request));
        verify(permissionRepository, never()).findByNameIn(any());
    }

    @Test
    void updateExitosoCambiaNombreYPermisos() {
        Role r = Role.builder().id(1L).name("OLD").description("d").build();
        when(roleRepository.findById(1L)).thenReturn(Optional.of(r));
        UpdateRoleRequest request = UpdateRoleRequest.builder().name("NEW").permissions(List.of("p1")).build();
        Permission p = Permission.builder().id(1L).name("p1").build();
        when(permissionRepository.findByNameIn(List.of("p1"))).thenReturn(List.of(p));
        when(roleRepository.save(r)).thenReturn(r);
        when(mapper.toResponse(r)).thenReturn(mock(RoleResponse.class));

        service.update(1L, request);

        assertEquals("NEW", r.getName());
    }

    @Test
    void updateNoEncontradoLanzaExcepcion() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateRoleRequest request = UpdateRoleRequest.builder().name("NEW").build();

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, request));
    }

    @Test
    void deleteExitosoYNoEncontrado() {
        when(roleRepository.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(roleRepository).deleteById(1L);

        when(roleRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }

    @Test
    void findAllPermissionsRetornaNombres() {
        when(permissionRepository.findAll()).thenReturn(List.of(
                Permission.builder().id(1L).name("users:read").build(),
                Permission.builder().id(2L).name("users:write").build()));

        List<String> result = service.findAllPermissions();

        assertEquals(2, result.size());
        assertEquals("users:read", result.get(0));
    }
}