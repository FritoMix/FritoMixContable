package com.fritomix.erp.modules.dispatch.api;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.modules.dispatch.application.dto.request.DispatchRequest;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse;
import com.fritomix.erp.modules.dispatch.application.service.DispatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dispatches")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_DISPATCHES_VIEW')")
    public ResponseEntity<PageResponse<DispatchResponse>> findAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "dispatchDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(dispatchService.findAll(search, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DISPATCHES_VIEW')")
    public ResponseEntity<DispatchResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(dispatchService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_DISPATCHES_CREATE')")
    public ResponseEntity<DispatchResponse> create(@Valid @RequestBody DispatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dispatchService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DISPATCHES_EDIT')")
    public ResponseEntity<DispatchResponse> update(@PathVariable Long id, @Valid @RequestBody DispatchRequest request) {
        return ResponseEntity.ok(dispatchService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DISPATCHES_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dispatchService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('PERMISSION_DISPATCHES_EDIT','PERMISSION_DISPATCHES_CHANGE_STATUS')")
    public ResponseEntity<DispatchResponse> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(dispatchService.updateStatus(id, status));
    }

    @GetMapping("/historial/pedido/{orderId}")
    @PreAuthorize("hasAuthority('PERMISSION_DISPATCHES_VIEW')")
    public ResponseEntity<List<DispatchResponse>> historyByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(dispatchService.findHistoryByOrderId(orderId));
    }

    @GetMapping("/historial")
    @PreAuthorize("hasAuthority('PERMISSION_DISPATCHES_VIEW')")
    public ResponseEntity<PageResponse<DispatchResponse>> historyByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @PageableDefault(size = 20, sort = "dispatchDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(dispatchService.findByDateRange(desde, hasta, pageable));
    }
}
