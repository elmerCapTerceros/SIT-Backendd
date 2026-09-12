package com.gobernacionSIT.sit_backend.controller;

import com.gobernacionSIT.sit_backend.entity.Rol;
import com.gobernacionSIT.sit_backend.service.RolService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Rol> crear(@RequestBody Rol rol) {
        Rol creado = rolService.crearRol(rol.getNombreRol());
        return ResponseEntity.ok(creado);
    }

    @GetMapping
    public ResponseEntity<List<Rol>> obtenerRoles() {
        List<Rol> roles = rolService.listarRoles();
        return ResponseEntity.ok(roles);
    }
}
    

