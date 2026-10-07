package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.entity.Rol;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.SolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AsignacionServiceImplTest {

    private final AsignacionTecnicoRepository asignacionRepository = mock(AsignacionTecnicoRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final AsignacionServiceImpl service = new AsignacionServiceImpl(
            asignacionRepository, mock(SolicitudRepository.class), usuarioRepository);

    @Test
    void tecnicoNoPuedeConsultarSolicitudesDeOtroTecnico() {
        when(usuarioRepository.findByUserLogin("tecnico-1")).thenReturn(Optional.of(usuario(1L, "TECNICO")));

        assertThrows(AccessDeniedException.class,
                () -> service.obtenerSolicitudesAsignadas(2L, "tecnico-1"));
        assertThrows(AccessDeniedException.class,
                () -> service.obtenerSolicitudesAsignadasPorEstado(2L, "PENDIENTE", "tecnico-1"));

        verify(asignacionRepository, never()).findByTecnicoId(2L);
        verify(asignacionRepository, never())
                .findByTecnico_IdAndSolicitud_Estado_EstadoSolicitud(2L, "PENDIENTE");
    }

    @Test
    void tecnicoPuedeConsultarSusPropiasSolicitudes() {
        when(usuarioRepository.findByUserLogin("tecnico-1")).thenReturn(Optional.of(usuario(1L, "TECNICO")));
        when(asignacionRepository.findByTecnicoId(1L)).thenReturn(List.of());

        assertTrue(service.obtenerSolicitudesAsignadas(1L, "tecnico-1").isEmpty());

        verify(asignacionRepository).findByTecnicoId(1L);
    }

    @Test
    void supervisorPuedeConsultarSolicitudesDeCualquierTecnico() {
        when(usuarioRepository.findByUserLogin("supervisor")).thenReturn(Optional.of(usuario(3L, "SUPERVISOR")));
        when(asignacionRepository.findByTecnicoId(1L)).thenReturn(List.of());

        assertTrue(service.obtenerSolicitudesAsignadas(1L, "supervisor").isEmpty());

        verify(asignacionRepository).findByTecnicoId(1L);
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
