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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> findAll(Pageable pageable) {
        Page<Long> ids = roleRepository.findIds(pageable);
        if (ids.isEmpty()) {
            return PageResponse.of(List.of(), ids.getNumber(), ids.getSize(), 0, 0);
        }
        Map<Long, Role> rolesById = roleRepository.findByIdsWithPermissions(ids.getContent()).stream()
                .collect(Collectors.toMap(Role::getId, Function.identity()));
        List<RoleResponse> content = ids.getContent().stream()
                .map(rolesById::get)
                .filter(java.util.Objects::nonNull)
                .map(mapper::toResponse)
                .toList();
        return PageResponse.of(content, ids.getNumber(), ids.getSize(), ids.getTotalElements(), ids.getTotalPages());
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
