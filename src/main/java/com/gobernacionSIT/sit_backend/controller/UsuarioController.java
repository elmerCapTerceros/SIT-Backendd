package com.gobernacionSIT.sit_backend.controller;

import com.gobernacionSIT.sit_backend.dto.request.RegistrarUsuarioRequest;
import com.gobernacionSIT.sit_backend.dto.request.ActualizarPerfilRequest;
import com.gobernacionSIT.sit_backend.dto.response.UsuarioResponse;
import com.gobernacionSIT.sit_backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistrarUsuarioRequest request) {
        UsuarioResponse response = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            @Valid @RequestBody ActualizarPerfilRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                usuarioService.actualizarPerfil(authentication.getName(), request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/tecnicos")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<List<UsuarioResponse>> listarTecnicos() {
        return ResponseEntity.ok(usuarioService.listarTecnicos());
    }

    @GetMapping("/area/{area}")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<List<UsuarioResponse>> listarPorArea(@PathVariable String area) {
        return ResponseEntity.ok(usuarioService.listarPorArea(area));
    }
}
