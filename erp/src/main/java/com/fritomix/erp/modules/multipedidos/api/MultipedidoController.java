package com.fritomix.erp.modules.multipedidos.api;

import com.fritomix.erp.modules.multipedidos.application.dto.request.CreateMultipedidoRequest;
import com.fritomix.erp.modules.multipedidos.application.dto.response.MultipedidoResponse;
import com.fritomix.erp.modules.multipedidos.application.service.MultipedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/multipedidos")
@RequiredArgsConstructor
public class MultipedidoController {

    private final MultipedidoService multipedidoService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('PERMISSION_MULTIPEDIDOS_CREATE', 'PERMISSION_ORDERS_EDIT', 'ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<MultipedidoResponse> create(@Valid @RequestBody CreateMultipedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(multipedidoService.createMultipedido(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PERMISSION_MULTIPEDIDOS_VIEW', 'PERMISSION_ORDERS_VIEW', 'ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<MultipedidoResponse>> findAll() {
        return ResponseEntity.ok(multipedidoService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PERMISSION_MULTIPEDIDOS_VIEW', 'PERMISSION_ORDERS_VIEW', 'ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<MultipedidoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(multipedidoService.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PERMISSION_MULTIPEDIDOS_DELETE', 'PERMISSION_ORDERS_EDIT', 'ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        multipedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
