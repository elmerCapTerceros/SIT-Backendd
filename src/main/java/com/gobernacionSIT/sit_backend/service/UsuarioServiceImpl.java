package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.RegistrarUsuarioRequest;
import com.gobernacionSIT.sit_backend.dto.request.ActualizarPerfilRequest;
import com.gobernacionSIT.sit_backend.dto.response.UsuarioResponse;
import com.gobernacionSIT.sit_backend.entity.Rol;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.repository.RolRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponse registrar(RegistrarUsuarioRequest request) {
        if (usuarioRepository.existsByUserLogin(request.getUserLogin())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese loginUser");
        }

        Rol rol = rolRepository.findByNombreRol("FUNCIONARIO")
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido"));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setUserLogin(request.getUserLogin());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setCargo(request.getCargo());
        usuario.setTelefono(request.getTelefono());
        usuario.setArea(request.getArea());
        usuario.setUbicacionOficina(request.getUbicacionOficina());
        usuario.setPrimerIngreso(true);
        usuario.setRol(rol);

        Usuario guardado = usuarioRepository.save(usuario);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizarPerfil(String userLogin, ActualizarPerfilRequest request) {
        Usuario usuario = usuarioRepository.findByUserLogin(userLogin)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setCargo(request.getCargo());
        usuario.setTelefono(request.getTelefono());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        usuario.setArea(request.getArea());
        usuario.setUbicacionOficina(request.getUbicacionOficina());
        usuario.setPrimerIngreso(false);

        return mapToResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esPrimerIngreso(String userLogin) {
        Usuario usuario = usuarioRepository.findByUserLogin(userLogin)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return usuario.isPrimerIngreso();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return mapToResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTecnicos() {
        return usuarioRepository.findByRol_NombreRol("TECNICO")
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarPorArea(String area) {
        return usuarioRepository.findByArea(area)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private UsuarioResponse mapToResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getUserLogin(),
            usuario.getCargo(),
            usuario.getTelefono(),
            usuario.getArea(),
            usuario.getUbicacionOficina(),
            usuario.getRol().getNombreRol(),
                usuario.getCreatedAt()
        );
    }
}
