package com.fritomix.erp.modules.roles.api;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.modules.roles.application.dto.request.CreateRoleRequest;
import com.fritomix.erp.modules.roles.application.dto.request.UpdateRoleRequest;
import com.fritomix.erp.modules.roles.application.dto.response.RoleResponse;
import com.fritomix.erp.modules.roles.application.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_ROLES_VIEW')")
    public ResponseEntity<PageResponse<RoleResponse>> findAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(roleService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ROLES_VIEW')")
    public ResponseEntity<RoleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_ROLES_CREATE')")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ROLES_EDIT')")
    public ResponseEntity<RoleResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ROLES_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_ROLES_VIEW')")
    public ResponseEntity<List<String>> findAllPermissions() {
        return ResponseEntity.ok(roleService.findAllPermissions());
    }
}
