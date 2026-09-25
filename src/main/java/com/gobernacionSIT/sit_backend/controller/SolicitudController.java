package com.gobernacionSIT.sit_backend.controller;

import com.gobernacionSIT.sit_backend.dto.request.AsignarSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.request.CrearSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.response.SolicitudResponse;
import com.gobernacionSIT.sit_backend.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SolicitudResponse> crear(
            @Valid @RequestBody CrearSolicitudRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitudService.crear(authentication.getName(), request));
    }

    @GetMapping("/misSolicitudes")
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'SUPERVISOR')")
    public ResponseEntity<List<SolicitudResponse>> listarPropias(Authentication authentication) {
        return ResponseEntity.ok(solicitudService.listarPropias(authentication.getName()));
    }

    @PutMapping("/{id}/asignacion")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<SolicitudResponse> asignar(
            @PathVariable Long id,
            @Valid @RequestBody AsignarSolicitudRequest request) {
        return ResponseEntity.ok(solicitudService.asignar(id, request));
    }

    @GetMapping("/SolicitudesTecnicas")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<List<SolicitudResponse>> listarTodas(Authentication authentication) {
        return ResponseEntity.ok(solicitudService.listarTodaas());
    }
}