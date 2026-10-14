package com.gobernacionSIT.sit_backend.controller;

import com.gobernacionSIT.sit_backend.dto.request.CrearReporteTrabajoRequest;
import com.gobernacionSIT.sit_backend.dto.response.ReporteTrabajoResponse;
import com.gobernacionSIT.sit_backend.service.ReporteTrabajoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteTrabajoController {

    private final ReporteTrabajoService reporteTrabajoService;

    @PostMapping
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<ReporteTrabajoResponse> crear(
            @Valid @RequestBody CrearReporteTrabajoRequest request,
            Authentication authentication) {
        ReporteTrabajoResponse response = reporteTrabajoService.crear(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<List<ReporteTrabajoResponse>> listarTodosReportes() {
        List<ReporteTrabajoResponse> reportes = reporteTrabajoService.listarTodosReportes();
        return ResponseEntity.ok(reportes);
    }

    @GetMapping("/mis-reportes")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<List<ReporteTrabajoResponse>> listarMisReportes(Authentication authentication) {
        List<ReporteTrabajoResponse> reportes =
                reporteTrabajoService.listarReportePorTecnico(authentication.getName());
        return ResponseEntity.ok(reportes);
    }
}
