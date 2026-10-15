package com.fritomix.erp.modules.camaras.api;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.modules.auth.application.dto.JwtUserInfo;
import com.fritomix.erp.modules.camaras.application.dto.request.*;
import com.fritomix.erp.modules.camaras.application.dto.response.CamaraHistorialResponse;
import com.fritomix.erp.modules.camaras.application.dto.response.CamaraVerificacionResponse;
import com.fritomix.erp.modules.camaras.application.service.CamaraService;
import com.fritomix.erp.modules.camaras.application.service.CamaraWordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/camaras")
@RequiredArgsConstructor
public class CamaraController {

    private final CamaraService camaraService;
    private final CamaraWordService camaraWordService;

    @GetMapping("/fichas")
    @PreAuthorize("hasAuthority('PERMISSION_CAMARAS_VIEW')")
    public ResponseEntity<PageResponse<CamaraVerificacionResponse>> getFichas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) String clienteNombre,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(camaraService.getFichas(estado, fechaInicio, fechaFin, clienteNombre, pageable));
    }

    @GetMapping("/fichas/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_CAMARAS_VIEW')")
    public ResponseEntity<CamaraVerificacionResponse> getFichaById(@PathVariable Long id) {
        return ResponseEntity.ok(camaraService.getFichaById(id));
    }

    @PostMapping("/fichas/{dispatchId}/iniciar")
    @PreAuthorize("hasAuthority('PERMISSION_CAMARAS_INICIAR')")
    public ResponseEntity<CamaraVerificacionResponse> iniciarVerificacion(
            @PathVariable Long dispatchId,
            @AuthenticationPrincipal JwtUserInfo currentUser) {
        return ResponseEntity.ok(camaraService.iniciarVerificacion(dispatchId, currentUser));
    }

    @PostMapping("/fichas/{id}/verificar")
    @PreAuthorize("hasAuthority('PERMISSION_CAMARAS_VERIFICAR')")
    public ResponseEntity<CamaraVerificacionResponse> registrarVerificacionCantidades(
            @PathVariable Long id,
            @Valid @RequestBody CamaraVerificarRequest request,
            @AuthenticationPrincipal JwtUserInfo currentUser) {
        return ResponseEntity.ok(camaraService.registrarVerificacionCantidades(id, request, currentUser));
    }

    @PostMapping("/fichas/{id}/observar")
    @PreAuthorize("hasAuthority('PERMISSION_CAMARAS_OBSERVAR')")
    public ResponseEntity<CamaraVerificacionResponse> observarFicha(
            @PathVariable Long id,
            @Valid @RequestBody CamaraObservarRequest request,
            @AuthenticationPrincipal JwtUserInfo currentUser) {
        return ResponseEntity.ok(camaraService.observarFicha(id, request, currentUser));
    }

    @PostMapping("/fichas/{id}/aprobar")
    @PreAuthorize("hasAuthority('PERMISSION_CAMARAS_APROBAR')")
    public ResponseEntity<CamaraVerificacionResponse> aprobarFicha(
            @PathVariable Long id,
            @Valid @RequestBody CamaraAprobarRequest request,
            @AuthenticationPrincipal JwtUserInfo currentUser) {
        return ResponseEntity.ok(camaraService.aprobarFicha(id, request, currentUser));
    }

    @PostMapping("/fichas/{id}/rechazar")
    @PreAuthorize("hasAuthority('PERMISSION_CAMARAS_RECHAZAR')")
    public ResponseEntity<CamaraVerificacionResponse> rechazarFicha(
            @PathVariable Long id,
            @Valid @RequestBody CamaraRechazarRequest request,
            @AuthenticationPrincipal JwtUserInfo currentUser) {
        return ResponseEntity.ok(camaraService.rechazarFicha(id, request, currentUser));
    }

    @PostMapping("/fichas/{id}/reabrir")
    @PreAuthorize("hasAuthority('PERMISSION_CAMARAS_ADMIN')")
    public ResponseEntity<CamaraVerificacionResponse> reabrirFicha(
            @PathVariable Long id,
            @Valid @RequestBody CamaraReabrirRequest request,
            @AuthenticationPrincipal JwtUserInfo currentUser) {
        return ResponseEntity.ok(camaraService.reabrirFicha(id, request, currentUser));
    }

    @GetMapping("/fichas/{id}/historial")
    @PreAuthorize("hasAuthority('PERMISSION_CAMARAS_HISTORIAL')")
    public ResponseEntity<List<CamaraHistorialResponse>> getHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(camaraService.getHistorial(id));
    }

    @GetMapping("/fichas/{id}/reporte")
    @PreAuthorize("hasAuthority('PERMISSION_CAMARAS_REPORTE')")
    public ResponseEntity<byte[]> descargarReporteWord(@PathVariable Long id) {
        CamaraVerificacionResponse v = camaraService.getFichaById(id);
        byte[] docxBytes = camaraWordService.generarActaVerificacionWord(v);

        String filename = String.format("Acta_Camaras_Despacho_%s.docx",
                v.dispatchNumber() != null ? v.dispatchNumber() : id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(docxBytes);
    }
}
