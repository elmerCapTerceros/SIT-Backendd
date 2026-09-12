package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.entity.Rol;
import com.gobernacionSIT.sit_backend.repository.RolRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RolService {
    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public Rol findById(Long id) {
        return rolRepository.findById(id).orElse(null);
    }

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    public Rol crearRol(String nombreRol){
         // 1. Validaciones de negocio
        if (nombreRol == null || nombreRol.isBlank()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }

        // 2. Regla de negocio: no duplicar
        if (rolRepository.existsByNombreRol(nombreRol)) {
            throw new IllegalStateException("Ya existe un rol con ese nombre");
        }

        // 3. Validar que sea uno de los permitidos
        /*if (!List.of("FUNCIONARIO", "TECNICO", "SUPERVISOR").contains(nombreRol)) {
            throw new IllegalArgumentException("Rol no permitido");
        }*/

        Rol rol = new Rol();
        rol.setNombreRol(nombreRol);
        return rolRepository.save(rol);
    }
}