package com.gobernacionSIT.sit_backend.controller;

import com.gobernacionSIT.sit_backend.dto.request.AvisoRequest;
import com.gobernacionSIT.sit_backend.dto.response.AvisoResponse;
import com.gobernacionSIT.sit_backend.service.AvisoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avisos")
@RequiredArgsConstructor
public class AvisoController {

    private final AvisoService avisoService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<AvisoResponse> crear(@Valid @RequestBody AvisoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avisoService.crear(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'TECNICO', 'SUPERVISOR')")
    public ResponseEntity<List<AvisoResponse>> listarActivos(Authentication authentication) {
        if (tieneRol(authentication, "SUPERVISOR")) {
            return ResponseEntity.ok(avisoService.listarActivos());
        }
        return ResponseEntity.ok(avisoService.listarPorDestinatario(
                destinatarioDe(authentication)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'TECNICO', 'SUPERVISOR')")
    public ResponseEntity<AvisoResponse> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        if (tieneRol(authentication, "SUPERVISOR")) {
            return ResponseEntity.ok(avisoService.obtenerPorId(id));
        }
        return ResponseEntity.ok(avisoService.obtenerPorId(id, destinatarioDe(authentication)));
    }

    @GetMapping("/destinatario/{destinatario}")
    @PreAuthorize("hasAnyRole('FUNCIONARIO', 'TECNICO', 'SUPERVISOR')")
    public ResponseEntity<List<AvisoResponse>> listarPorDestinatario(
            @PathVariable String destinatario,
            Authentication authentication) {
        if (!tieneRol(authentication, "SUPERVISOR")
                && !destinatario.equals(destinatarioDe(authentication))) {
            throw new AccessDeniedException("No tiene permiso para consultar este público");
        }
        return ResponseEntity.ok(avisoService.listarPorDestinatario(destinatario));
    }

    @GetMapping("/supervisor/{idSupervisor}")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<List<AvisoResponse>> listarPorSupervisor(
            @PathVariable Long idSupervisor) {
        return ResponseEntity.ok(avisoService.listarPorSupervisor(idSupervisor));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<AvisoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AvisoRequest request) {
        return ResponseEntity.ok(avisoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        avisoService.cambiarEstado(id, activo);
        return ResponseEntity.noContent().build();
    }

    private String destinatarioDe(Authentication authentication) {
        if (tieneRol(authentication, "FUNCIONARIO")) {
            return "FUNCIONARIO";
        }
        if (tieneRol(authentication, "TECNICO")) {
            return "TECNICO";
        }
        throw new AccessDeniedException("El usuario no tiene un rol habilitado para consultar avisos");
    }

    private boolean tieneRol(Authentication authentication, String rol) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + rol));
    }
}
