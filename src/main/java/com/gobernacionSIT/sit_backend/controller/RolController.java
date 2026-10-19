package com.gobernacionSIT.sit_backend.controller;

import com.gobernacionSIT.sit_backend.dto.request.CrearRolRequest;
import com.gobernacionSIT.sit_backend.entity.Rol;
import com.gobernacionSIT.sit_backend.service.RolService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {
    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<Rol> crear(@Valid @RequestBody CrearRolRequest request) {
        Rol creado = rolService.crearRol(request.getNombreRol());
        return ResponseEntity.ok(creado);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<List<Rol>> obtenerRoles() {
        List<Rol> roles = rolService.listarRoles();
        return ResponseEntity.ok(roles);
    }
}
    
