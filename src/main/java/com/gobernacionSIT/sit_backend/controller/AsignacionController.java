package com.gobernacionSIT.sit_backend.controller;
import com.gobernacionSIT.sit_backend.dto.request.AsignarTecnicoRequest;
import com.gobernacionSIT.sit_backend.dto.response.SolicitudResponse;
import com.gobernacionSIT.sit_backend.service.AsignacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {
    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<Void> asignar(@Valid @RequestBody AsignarTecnicoRequest request) {
        asignacionService.asignar(request.getSolicitudId(), request.getTecnicoId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/tecnicos")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<List<Long>> listarTecnicos() {
        return ResponseEntity.ok(asignacionService.listarTecnicos());
    }

    @GetMapping("/solicitud/{solicitudId}/tecnicos")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'TECNICO')")
    public ResponseEntity<List<Long>> obtenerTecnicosAsignados(@PathVariable Long solicitudId) {
        return ResponseEntity.ok(asignacionService.obtenerTecnicosAsignados(solicitudId));
    }

    @GetMapping("/tecnico/{tecnicoId}/solicitudes")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'TECNICO')")
    public ResponseEntity<List<SolicitudResponse>> obtenerSolicitudesAsignadas(
            @PathVariable Long tecnicoId,
            Authentication authentication) {
        return ResponseEntity.ok(
                asignacionService.obtenerSolicitudesAsignadas(tecnicoId, authentication.getName())
        );
    }

    @GetMapping("/tecnico/{tecnicoId}/solicitudes/estado/{estadoSolicitud}")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'TECNICO')")
    public ResponseEntity<List<SolicitudResponse>> obtenerSolicitudesAsignadasPorEstado(
            @PathVariable Long tecnicoId,
            @PathVariable String estadoSolicitud,
            Authentication authentication) {
        return ResponseEntity.ok(
                asignacionService.obtenerSolicitudesAsignadasPorEstado(
                        tecnicoId, estadoSolicitud, authentication.getName()
                )
        );
    }
}
