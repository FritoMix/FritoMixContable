package com.fritomix.erp.modules.drivers.api;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.modules.drivers.application.dto.request.DriverRequest;
import com.fritomix.erp.modules.drivers.application.dto.response.DriverResponse;
import com.fritomix.erp.modules.drivers.application.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_DRIVERS_VIEW')")
    public ResponseEntity<PageResponse<DriverResponse>> findAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(driverService.findAll(search, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DRIVERS_VIEW')")
    public ResponseEntity<DriverResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_DRIVERS_CREATE')")
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody DriverRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DRIVERS_EDIT')")
    public ResponseEntity<DriverResponse> update(@PathVariable Long id, @Valid @RequestBody DriverRequest request) {
        return ResponseEntity.ok(driverService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DRIVERS_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
