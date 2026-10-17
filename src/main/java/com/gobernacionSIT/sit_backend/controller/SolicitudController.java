package com.gobernacionSIT.sit_backend.controller;

import com.gobernacionSIT.sit_backend.dto.request.AsignarSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.request.CrearSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.request.RechazarSolicitudRequest;
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

    @PatchMapping("/{id}/atender")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<SolicitudResponse> atender(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(solicitudService.atender(id, authentication.getName()));
    }

    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<SolicitudResponse> rechazar(
            @PathVariable Long id,
            @Valid @RequestBody RechazarSolicitudRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(solicitudService.rechazar(id, authentication.getName(), request));
    }

    @PatchMapping("/{id}/concluir")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<SolicitudResponse> concluir(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(solicitudService.concluir(id, authentication.getName()));
    }

    @PatchMapping("/{id}/verificar")
    @PreAuthorize("hasRole('FUNCIONARIO')")
    public ResponseEntity<SolicitudResponse> verificar(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(solicitudService.verificar(id, authentication.getName()));
    }

    @GetMapping("/SolicitudesTecnicas")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<List<SolicitudResponse>> listarTodas() {
        return ResponseEntity.ok(solicitudService.listarTodas());
    }
}