package com.fritomix.erp.modules.dispatch.api;

import com.fritomix.erp.modules.dispatch.application.dto.request.DispatchRequest;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse;
import com.fritomix.erp.modules.dispatch.application.service.DispatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dispatches")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_DISPATCHES_VIEW')")
    public ResponseEntity<List<DispatchResponse>> findAll() {
        return ResponseEntity.ok(dispatchService.findAll());
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
    @PreAuthorize("hasAuthority('PERMISSION_DISPATCHES_EDIT')")
    public ResponseEntity<DispatchResponse> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(dispatchService.updateStatus(id, status));
    }
}
