package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.entity.Rol;
import com.gobernacionSIT.sit_backend.exception.BusinessException;
import com.gobernacionSIT.sit_backend.exception.ResourceNotFoundException;
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
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
    }

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    public Rol crearRol(String nombreRol) {
        if (nombreRol == null || nombreRol.isBlank()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }

        String nombreNormalizado = nombreRol.trim().toUpperCase(java.util.Locale.ROOT);
        if (!List.of("FUNCIONARIO", "TECNICO", "SUPERVISOR").contains(nombreNormalizado)) {
            throw new IllegalArgumentException("El rol solicitado no está permitido");
        }
        if (rolRepository.existsByNombreRol(nombreNormalizado)) {
            throw new BusinessException("Ya existe un rol con ese nombre");
        }

        Rol rol = new Rol();
        rol.setNombreRol(nombreNormalizado);
        return rolRepository.save(rol);
    }
}