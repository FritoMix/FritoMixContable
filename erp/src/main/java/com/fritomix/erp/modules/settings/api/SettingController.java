package com.fritomix.erp.modules.settings.api;

import com.fritomix.erp.modules.settings.application.dto.request.SettingRequest;
import com.fritomix.erp.modules.settings.application.dto.response.SettingResponse;
import com.fritomix.erp.modules.settings.application.service.SettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_SETTINGS_VIEW')")
    public ResponseEntity<SettingResponse> get() {
        return ResponseEntity.ok(settingService.get());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('PERMISSION_SETTINGS_EDIT')")
    public ResponseEntity<SettingResponse> update(@Valid @RequestBody SettingRequest request) {
        return ResponseEntity.ok(settingService.update(request));
    }
}
