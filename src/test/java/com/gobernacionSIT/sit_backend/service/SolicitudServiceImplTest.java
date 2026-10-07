package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.AsignarSolicitudRequest;
import com.gobernacionSIT.sit_backend.entity.Rol;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.exception.BusinessException;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.EstadoSolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.SolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolicitudServiceImplTest {

    private final SolicitudRepository solicitudRepository = mock(SolicitudRepository.class);
    private final AsignacionTecnicoRepository asignacionRepository = mock(AsignacionTecnicoRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final SolicitudServiceImpl service = new SolicitudServiceImpl(
            solicitudRepository,
            usuarioRepository,
            mock(EstadoSolicitudRepository.class),
            asignacionRepository);

    @Test
    void rechazaAsignacionCuandoUsuarioNoEsTecnico() {
        AsignarSolicitudRequest request = new AsignarSolicitudRequest();
        request.setTecnicoId(2L);
        request.setPrioridad("MEDIA");
        when(solicitudRepository.findById(5L)).thenReturn(Optional.of(new Solicitud()));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, "FUNCIONARIO")));

        assertThrows(BusinessException.class, () -> service.asignar(5L, request));

        verify(solicitudRepository, never()).save(any(Solicitud.class));
        verify(asignacionRepository, never()).save(any());
    }

    private Usuario usuario(Long id, String nombreRol) {
        Rol rol = new Rol();
        rol.setNombreRol(nombreRol);
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setRol(rol);
        return usuario;
    }
}
