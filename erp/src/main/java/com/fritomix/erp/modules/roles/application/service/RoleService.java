package com.fritomix.erp.modules.roles.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.auth.domain.entity.Permission;
import com.fritomix.erp.modules.auth.domain.entity.Role;
import com.fritomix.erp.modules.auth.domain.repository.RoleRepository;
import com.fritomix.erp.modules.roles.application.dto.request.CreateRoleRequest;
import com.fritomix.erp.modules.roles.application.dto.request.UpdateRoleRequest;
import com.fritomix.erp.modules.roles.application.dto.response.RoleResponse;
import com.fritomix.erp.modules.roles.application.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper mapper;

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoleResponse findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
        return mapper.toResponse(role);
    }

    @Transactional
    public RoleResponse create(CreateRoleRequest request) {
        Role role = Role.builder()
                .name(request.name())
                .description(request.description())
                .build();

        if (request.permissions() != null) {
            List<Permission> perms = permissionRepository.findByNameIn(request.permissions());
            role.setPermissions(perms);
        }

        role = roleRepository.save(role);
        return mapper.toResponse(role);
    }

    @Transactional
    public RoleResponse update(Long id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));

        if (request.name() != null) role.setName(request.name());
        if (request.description() != null) role.setDescription(request.description());
        if (request.permissions() != null) {
            List<Permission> perms = permissionRepository.findByNameIn(request.permissions());
            role.setPermissions(perms);
        }

        role = roleRepository.save(role);
        return mapper.toResponse(role);
    }

    @Transactional
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rol no encontrado con id: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<String> findAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
    }
}
