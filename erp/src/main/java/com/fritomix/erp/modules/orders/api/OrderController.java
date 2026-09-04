package com.fritomix.erp.modules.orders.api;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.modules.orders.application.dto.request.OrderRequest;
import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse;
import com.fritomix.erp.modules.orders.application.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/next-number")
    @PreAuthorize("hasAnyAuthority('PERMISSION_ORDERS_VIEW','PERMISSION_ORDERS_CREATE')")
    public ResponseEntity<String> nextOrderNumber() {
        return ResponseEntity.ok(orderService.generateNextOrderNumber());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_ORDERS_VIEW')")
    public ResponseEntity<PageResponse<OrderResponse>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) List<String> statuses,
            @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(search, status, statuses, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ORDERS_VIEW')")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_ORDERS_CREATE')")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ORDERS_EDIT')")
    public ResponseEntity<OrderResponse> update(@PathVariable Long id, @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('PERMISSION_ORDERS_EDIT','PERMISSION_ORDERS_CHANGE_STATUS')")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/production-status")
    @PreAuthorize("hasAuthority('PERMISSION_ORDERS_PRODUCTION')")
    public ResponseEntity<OrderResponse> updateProductionStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateProductionStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ORDERS_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
